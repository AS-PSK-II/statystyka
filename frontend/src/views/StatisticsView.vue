<template>
  <v-card class="pa-6 mt-3 fancy-card" elevation="0">
    <v-row>
      <v-col cols="12" md="12">
        <v-select
            v-model="selectedAttribute"
            :items="airportOptions"
            label="Wybierz lotnisko"
            variant="solo-filled"
            rounded="lg"
            prepend-inner-icon="mdi-airplane"
        >
        </v-select>
        <v-data-table
            class="mt-0 pt-0"
            :headers="headers"
            :items="items"
            :items-per-page="12"
            :items-per-page-options="[3,6,9,12]"
        >
          <template v-slot:item.name="{ item }">
            <strong>{{ getName(item.name) }}</strong>
          </template>
        </v-data-table>
      </v-col>
    </v-row>
  </v-card>
</template>

<script>

export default {
  name: 'StatisticsView',
  components: {},
  data () {
    return {
      selectedAttribute: 'Chopina w Warszawie',
      headers: [
        { title: '', value: 'name' },
        { title: '', value: 'value' }
      ]
    }
  },
  props: {
    statisticsData: {
      type: Array,
      required: true
    },
    airportOptions: {
      type: Array,
      required: true
    }
  },
  computed: {
    items () {
      for (let i = 0; i < this.statisticsData.length; i++) {
        const stat = this.statisticsData[i]
        if (stat.id === this.selectedAttribute) {
          return stat.values
        }
      }
    }
  },
  watch: {
  },
  methods: {
    getName (nameValue) {
      switch (nameValue) {
        case 'std':
          return 'Odchylenia standardowe'
        case 'avg':
          return 'Średnia'
        case 'min':
          return 'Minimum'
        case 'max':
          return 'Maximum'
        case 'kurtosis':
          return 'Kurtoza'
        case 'skewness':
          return 'Skośność'
        case 'dominant':
          return 'Moda'
        case 'median':
          return 'Mediana'
        case 'diff':
          return 'Rozstęp'
        case 'firstQuantile':
          return 'Q1'
        case 'secondQuantile':
          return 'Q2'
        case 'thirdQuantile':
          return 'Q3'
        default:
          return nameValue
      }
    }
  }
}
</script>