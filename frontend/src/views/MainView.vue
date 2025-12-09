<template>
  <div>
    <v-tabs v-model="tabIndex">
      <v-tab>Ładowanie danych</v-tab>
      <v-tab v-if="dataLoaded">Wykresy</v-tab>
      <v-tab v-if="dataLoaded">Statystyki</v-tab>
      <v-tab v-if="dataLoaded">Twarze Charnoffa</v-tab>
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
        </v-card-text>
      </v-tabs-window-item>
      <v-tabs-window-item v-if="dataLoaded">
        <charts-view :key="refreshViews" :chart-data="fullData.rowData"></charts-view>
      </v-tabs-window-item>
      <v-tabs-window-item v-if="dataLoaded">
        <statistics-view :key="refreshViews" :statistics-data="fullData.statisticsData"></statistics-view>
      </v-tabs-window-item>
    </v-tabs-window>
  </div>
</template>

<script>
import ChartsView from "@/views/ChartsView.vue";
import LoadFileView from "@/views/LoadFileView.vue";
import StatisticsView from "@/views/StatisticsView.vue";


export default {
  name: 'MainView',
  components: {StatisticsView, LoadFileView, ChartsView},
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

      this.headers = [{title: 'Kwartał i rok', key: 'date', sortable: false}]
      this.items = []
      for (let i = 0; i < this.fullData.rowData.length; i++) {

        let item = {
          date: this.fullData.rowData[i].timestamp
        }
        for (let j = 0; j < this.fullData.rowData[i].dataPoints.length; j++) {
          if (i === 0) {
            this.headers.push({
                  title: this.fullData.rowData[i].dataPoints[j].id,
                  key: this.fullData.rowData[i].dataPoints[j].id
                }
            )
          }
          item[this.fullData.rowData[i].dataPoints[j].id] = this.fullData.rowData[i].dataPoints[j].value
        }
        this.items.push(item)
      }
      this.refreshViews++
    }
  }
}
</script>
