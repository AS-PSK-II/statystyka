<template>
  <v-container>
    <div class="display-1 mb-6 page-title">Wykresy ruchu pasażerskiego</div>

    <CsvUploader @uploaded="loadSample" />

    <v-row class="mb-6" v-if="kpis.length">
      <v-col cols="12" md="4" v-for="(k, i) in kpis" :key="i">
        <v-card class="kpi-card" elevation="0">
          <div class="text-caption text-medium-emphasis">{{ k.label }}</div>
          <div class="text-h5 mt-1">{{ k.value }}</div>
          <div class="text-caption mt-1">{{ k.sub }}</div>
        </v-card>
      </v-col>
    </v-row>

    <v-row class="mb-2" v-if="series.length">
      <v-col cols="12" class="d-flex align-center">
        <v-chip-group v-model="activeAirports" multiple column class="mr-3">
          <v-chip
              v-for="id in airports"
              :key="id"
              :value="id"
              filter
              variant="elevated"
              rounded="lg"
          >
            {{ id }}
          </v-chip>
        </v-chip-group>
        <v-spacer />
        <v-btn-toggle v-model="chartKind" rounded="lg" color="primary" density="comfortable">
          <v-btn value="bar"><v-icon>mdi-chart-bar</v-icon></v-btn>
          <v-btn value="line"><v-icon>mdi-chart-line</v-icon></v-btn>
          <v-btn value="radar"><v-icon>mdi-radar</v-icon></v-btn>
        </v-btn-toggle>
      </v-col>
    </v-row>

    <v-row v-if="series.length">
      <v-col cols="12">
        <ChartCard
            :title="'Trend: kwartalne wartości'"
            :type="chartKind"
            :chartData="timeChart"
            :options="{ animation: { duration: 700 } }"
        />
      </v-col>

      <v-col cols="12" md="6">
        <ChartCard :title="'Top lotniska (ostatni kwartał)'" type="bar" :chartData="topChart" />
      </v-col>

      <v-col cols="12" md="6">
        <ChartCard :title="'Udział (%) – ostatni kwartał'" type="doughnut" :chartData="shareChart" />
      </v-col>
    </v-row>

    <v-alert v-else type="info" variant="tonal" class="mt-6">
      Kliknij „Załaduj próbkę”, aby wczytać dane demonstracyjne.
    </v-alert>
  </v-container>
</template>

<script>
import CsvUploader from '@/components/CsvUploader.vue'
import ChartCard from '@/components/ChartCard.vue'

const SAMPLE = [
  {
    timestamp: 'IV kwartał 2022',
    dataPoints: [
      { id: 'Chopina w Warszawie', value: 4898107.0 },
      { id: 'Kraków Balice', value: 2236123.0 },
      { id: 'Gdańsk im. L. Wałęsy', value: 1588483.0 },
      { id: 'Katowice - Pyrzowice', value: 1886490.0 },
      { id: 'Wrocław - Strachowice', value: 1066189.0 }
    ]
  },
  {
    timestamp: 'III kwartał 2022',
    dataPoints: [
      { id: 'Chopina w Warszawie', value: 4898107.0 },
      { id: 'Kraków Balice', value: 2236123.0 },
      { id: 'Gdańsk im. L. Wałęsy', value: 1588483.0 },
      { id: 'Katowice - Pyrzowice', value: 1886490.0 },
      { id: 'Wrocław - Strachowice', value: 1066189.0 }
    ]
  }
]

export default {
  name: 'ChartsView',
  components: { CsvUploader, ChartCard },
  data () {
    return {
      series: [],
      airports: [],
      activeAirports: [],
      chartKind: 'line',
      palette: ['#6366F1','#22C1C3','#EF4444','#F59E0B','#10B981','#8B5CF6','#3B82F6','#EC4899','#14B8A6','#84CC16']
    }
  },
  computed: {
    kpis () {
      if (!this.series.length) return []
      const last = this.series[0]
      const sum = last.dataPoints.reduce((s, d) => s + d.value, 0)
      const max = last.dataPoints.reduce((m, d) => (d.value > m.value ? d : m), { id: '', value: -1 })
      return [
        { label: 'Łącznie (ostatni kwartał)', value: this.fmt(sum), sub: last.timestamp },
        { label: 'Największy port', value: max.id, sub: this.fmt(max.value) },
        { label: 'Liczba portów', value: String(last.dataPoints.length), sub: 'aktywne w kwartale' }
      ]
    },
    timeChart () {
      if (!this.series.length) return { labels: [], datasets: [] }
      const labels = this.series.map(s => s.timestamp)
      const selected = this.activeAirports.length ? this.activeAirports : this.airports
      const datasets = selected.map((id, i) => ({
        label: id,
        data: this.series.map(s => s.dataPoints.find(d => d.id === id)?.value || 0),
        backgroundColor: this.palette[i % this.palette.length] + '55',
        borderColor: this.palette[i % this.palette.length],
        borderWidth: 2,
        pointRadius: 2,
        fill: this.chartKind !== 'line'
      }))
      return { labels, datasets }
    },
    topChart () {
      if (!this.series.length) return { labels: [], datasets: [] }
      const last = this.series[0]
      const sorted = [...last.dataPoints].sort((a, b) => b.value - a.value).slice(0, 5)
      return {
        labels: sorted.map(d => d.id),
        datasets: [{ label: 'Pasażerowie', data: sorted.map(d => d.value), backgroundColor: this.palette.slice(0, 5) }]
      }
    },
    shareChart () {
      if (!this.series.length) return { labels: [], datasets: [] }
      const last = this.series[0]
      const total = last.dataPoints.reduce((s, d) => s + d.value, 0)
      return {
        labels: last.dataPoints.map(d => d.id),
        datasets: [{
          data: last.dataPoints.map(d => +(d.value / total * 100).toFixed(2)),
          backgroundColor: this.palette.slice(0, last.dataPoints.length)
        }]
      }
    }
  },
  methods: {
    loadSample () {
      // „na sztywno”
      this.series = JSON.parse(JSON.stringify(SAMPLE))
      this.airports = Array.from(new Set(SAMPLE.flatMap(s => s.dataPoints.map(d => d.id))))
      this.activeAirports = this.airports.slice(0, 3)
    },
    fmt (n) { return new Intl.NumberFormat('pl-PL').format(Math.round(n)) }
  }
}
</script>
