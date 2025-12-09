<template>
  <v-card class="pa-6 fancy-card" elevation="0">
    <v-row>
      <v-col cols="12" md="12">
        <pre>{{statisticsData}}</pre>
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
      tabIndex: 0
    }
  },
  props: {
    statisticsData: {
      type: Array,
      required: true
    }
  },
  computed: {
  },
  watch: {
  },
  methods: {
  }
}
</script>