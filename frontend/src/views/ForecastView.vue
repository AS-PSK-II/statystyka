<template>
  <v-container>
    <div class="display-1 mb-6 page-title">Prognozowanie</div>

    <!-- Panel sterujący -->
    <v-card class="pa-5 fancy-card mb-6" elevation="0">
      <v-row>
        <v-col cols="12" md="5">
          <v-select
              v-model="selectedAirport"
              :items="airports"
              label="Wybierz lotnisko"
              variant="solo-filled"
              rounded="lg"
              prepend-inner-icon="mdi-airplane"
          />
        </v-col>

        <v-col cols="12" md="4">
          <div class="text-caption text-medium-emphasis mb-1">
            Kroki prognozy (ile kwartałów wstecz)
          </div>
          <v-slider
              v-model="windowSize"
              :min="2"
              :max="10"
              :step="1"
              thumb-label
          />
        </v-col>

        <v-col cols="12" md="3" class="d-flex align-end">
          <v-btn
              color="primary"
              block
              rounded
              height="44"
              @click="generateForecast"
          >
            <v-icon start>mdi-play</v-icon>
            Generuj prognozę
          </v-btn>
        </v-col>
      </v-row>

      <v-alert type="info" variant="tonal" class="mt-3">
        Dla każdego punktu prognozy obliczamy średnią z poprzednich
        {{ windowSize }} wartości dla wybranego lotniska.
      </v-alert>
    </v-card>

    <!-- KPI -->
    <v-row class="mb-6" v-if="kpis.length">
      <v-col cols="12" md="4" v-for="(k, i) in kpis" :key="i">
        <v-card class="kpi-card" elevation="0">
          <div class="text-caption text-medium-emphasis">{{ k.label }}</div>
          <div class="text-h5 mt-1">{{ k.value }}</div>
          <div class="text-caption mt-1">{{ k.sub }}</div>
        </v-card>
      </v-col>
    </v-row>

    <ChartCard
        v-if="forecastChart"
        :key="chartRefreshKey"
        :title="`Prognoza - ${selectedAirport}`"
        type="line"
        :chartData="forecastChart"
        :options="chartOptions"
    />

    <v-alert v-else type="info" variant="tonal" class="mt-6">
      Załaduj dane i wybierz lotnisko, aby wygenerować prognozę.
    </v-alert>
  </v-container>
</template>

<script>
import ChartCard from '@/components/ChartCard.vue'

export default {
  name: 'ForecastView',
  components: { ChartCard },
  props: {
    chartData: {
      type: Array,
      required: true
    }
  },
  data () {
    return {
      series: [],
      airports: [],
      selectedAirport: null,
      windowSize: 4,      // ile kwartałów wstecz w średniej
      forecastHorizon: 8, // ile kwartałów do przodu prognozujemy
      forecastChart: null,
      chartRefreshKey: 0,
      chartOptions: {
        animation: { duration: 700 }
      }
    }
  },
  created () {
    if (this.chartData && this.chartData.length) {
      this.initFromChartData()
    }
  },
  watch: {
    chartData: {
      handler () {
        this.initFromChartData()
      },
      deep: true
    }
  },
  computed: {
    kpis () {
      if (!this.series.length || !this.selectedAirport) return []
      const values = this.getHistoryValues(this.selectedAirport)
      if (!values.length) return []
      const lastReal = values[values.length - 1]
      return [
        {
          label: 'Punkty historyczne',
          value: values.length,
          sub: 'liczba kwartałów'
        },
        {
          label: 'Kroki prognozy',
          value: this.windowSize,
          sub: 'ile kwartałów wstecz'
        },
        {
          label: 'Ostatnia wartość',
          value: this.fmt(lastReal),
          sub: this.lastTimestampLabel
        }
      ]
    },
    lastTimestampLabel () {
      if (!this.series.length) return ''
      return this.series[this.series.length - 1].timestamp
    }
  },
  methods: {
    initFromChartData () {
      // kopia + sortowanie rosnąco po czasie
      const clone = JSON.parse(JSON.stringify(this.chartData || []))
      this.series = this.sortByTimestamp(clone)
      this.airports = Array.from(
          new Set(this.series.flatMap(s => s.dataPoints.map(d => d.id)))
      )
      this.selectedAirport = this.airports[0] || null
      // this.generateForecast()
      // this.chartRefreshKey++
    },

    // "III kwartał 2020" → sort po roku, potem kwartale
    sortByTimestamp (data) {
      const romanMap = { I: 1, II: 2, III: 3, IV: 4 }
      return data.sort((a, b) => {
        const [rA,, yA] = a.timestamp.split(' ')
        const [rB,, yB] = b.timestamp.split(' ')
        const yearA = Number(yA)
        const yearB = Number(yB)
        const qA = romanMap[rA] || 0
        const qB = romanMap[rB] || 0
        if (yearA !== yearB) return yearA - yearB
        return qA - qB
      })
    },

    getHistoryValues (airportId) {
      return this.series.map(s => {
        const dp = s.dataPoints.find(d => d.id === airportId)
        return dp ? dp.value : 0
      })
    },

    nextTimestamps (count) {
      if (!this.series.length) return []
      const roman = ['I', 'II', 'III', 'IV']
      const last = this.series[this.series.length - 1].timestamp
      const [rLast,, yLast] = last.split(' ')
      let q = roman.indexOf(rLast) // 0..3
      let year = Number(yLast)
      const result = []
      for (let i = 0; i < count; i++) {
        q++
        if (q > 3) {
          q = 0
          year++
        }
        result.push(`${roman[q]} kwartał ${year}`)
      }
      return result
    },

    generateForecast () {
      if (!this.selectedAirport || !this.series.length) {
        this.forecastChart = null
        return
      }
      const history = this.getHistoryValues(this.selectedAirport)
      if (history.length <= this.windowSize) {
        this.forecastChart = null
        return
      }

      const allValues = [...history]
      const forecasts = []

      // prosta średnia krocząca
      for (let i = 0; i < this.forecastHorizon; i++) {
        const window = allValues.slice(-this.windowSize)
        const mean = window.reduce((s, v) => s + v, 0) / window.length
        forecasts.push(mean)
        allValues.push(mean)
      }

      const labels = this.series.map(s => s.timestamp)
          .concat(this.nextTimestamps(this.forecastHorizon))

      const realDataset = {
        label: 'Rzeczywiste',
        data: [
          ...history,
          ...Array(this.forecastHorizon).fill(null)
        ],
        borderColor: '#3B82F6',
        backgroundColor: '#3B82F6aa',
        borderWidth: 2,
        pointRadius: 3,
        tension: 0.3
      }

      const forecastDataset = {
        label: 'Prognoza',
        data: [
          ...Array(history.length - 1).fill(null),
          history[history.length - 1],
          ...forecasts
        ],
        borderColor: '#FACC15',
        backgroundColor: '#FACC1588',
        borderDash: [6, 4],
        pointRadius: 3,
        tension: 0.3
      }

      this.forecastChart = { labels, datasets: [realDataset, forecastDataset] }
      this.chartRefreshKey++
    },

    fmt (n) {
      return new Intl.NumberFormat('pl-PL').format(Math.round(n))
    }
  }
}
</script>

<style scoped>
.page-title{
  font-weight: 700;
  letter-spacing: .2px;
}
.fancy-card{
  border-radius: 18px;
  background: linear-gradient(180deg, rgba(15,23,42,.9), rgba(15,23,42,.95));
  border: 1px solid rgba(148,163,184,.4);
}
.kpi-card{
  border-radius: 18px;
  padding: 18px;
  background: linear-gradient(180deg, rgba(15,23,42,.9), rgba(15,23,42,.95));
  border: 1px solid rgba(51,65,85,.8);
}
</style>
