<template>
  <div>
    <v-tabs height="55" v-model="tabIndex" style="border-radius: 10px; margin-left: 10px; margin-right: 15px; background: linear-gradient(180deg, rgba(99,102,241,.12), rgba(99,102,241,.06))">
      <v-tab :class="{'font-weight-bold': tabIndex === 0}">Ładowanie danych</v-tab>
      <v-tab :class="{'font-weight-bold': tabIndex === 1}" v-if="dataLoaded">Wykresy</v-tab>
      <v-tab :class="{'font-weight-bold': tabIndex === 2}" v-if="dataLoaded">Statystyki</v-tab>
      <v-tab :class="{'font-weight-bold': tabIndex === 3}" v-if="dataLoaded">Prognoza</v-tab>
      <v-tab :class="{'font-weight-bold': tabIndex === 4}" v-if="dataLoaded">Twarze Chernoffa</v-tab>
    </v-tabs>
    <v-tabs-window v-model="tabIndex">
      <v-tabs-window-item>
        <v-card-text>
          <load-file-view @loaded="fullDataLoaded"></load-file-view>
          <v-data-table
              v-if="dataLoaded"
              :headers="headers"
              :items="items"
          >
          </v-data-table>
          <v-card-actions class="float-right" @click="refreshViews++; tabIndex = 1">
            <v-btn block density="compact" color="primary" v-if="dataLoaded">
              Przejdź dalej
            </v-btn>
          </v-card-actions>
        </v-card-text>
      </v-tabs-window-item>
      <v-tabs-window-item v-if="dataLoaded">
        <charts-view :key="refreshViews" :chart-data="fullData.rowData"></charts-view>
      </v-tabs-window-item>
      <v-tabs-window-item v-if="dataLoaded">
        <statistics-view :key="refreshViews" :statistics-data="fullData.statisticsData" :airport-options="airportOptions"></statistics-view>
      </v-tabs-window-item>
      <v-tabs-window-item v-if="dataLoaded">
        <forecast-view :key="refreshViews" :chart-data="fullData.rowData"></forecast-view>
      </v-tabs-window-item>
      <v-tabs-window-item v-if="dataLoaded">
        <chernoff-faces-view :key="refreshViews" :chart-data="fullData.rowData" :statistics-data="fullData.statisticsData"></chernoff-faces-view>
      </v-tabs-window-item>
    </v-tabs-window>
  </div>
</template>

<script>
import ChartsView from "@/views/ChartsView.vue";
import LoadFileView from "@/views/LoadFileView.vue";
import StatisticsView from "@/views/StatisticsView.vue";
import ForecastView from "@/views/ForecastView.vue";
import ChernoffFaceViewsView from "@/views/ChernoffFaceView.vue";

export default {
  name: 'MainView',
  components: {ChernoffFacesView: ChernoffFaceViewsView, ForecastView, StatisticsView, LoadFileView, ChartsView},
  data () {
    return {
      tabIndex: 0,
      dataLoaded: false,
      refreshViews: 0,
      fullData: {
        rowData: [],
        statisticsData: []
      },
      headers: [],
      airportOptions: [],
      items: []
    }
  },
  computed: {
  },
  watch: {
  },
  methods: {
    fullDataLoaded (fullData) {
      this.dataLoaded = true
      this.fullData = Object.assign(fullData)

      this.headers = [{title: 'Kwartał i rok', value: 'date', sortable: false}]
      this.items = []
      for (let i = 0; i < this.fullData.rowData.length; i++) {

        let item = {
          date: this.fullData.rowData[i].timestamp
        }
        for (let j = 0; j < this.fullData.rowData[i].dataPoints.length; j++) {
          if (i === 0) {
            this.headers.push({
                  title: this.fullData.rowData[i].dataPoints[j].id,
                  value: this.fullData.rowData[i].dataPoints[j].id
                }
            )
          }
          item[this.fullData.rowData[i].dataPoints[j].id] = this.fullData.rowData[i].dataPoints[j].value
        }
        this.items.push(item)
      }
      this.airportOptions = this.headers.slice(1, this.headers.length)
      this.refreshViews++
    }
  }
}
</script>
