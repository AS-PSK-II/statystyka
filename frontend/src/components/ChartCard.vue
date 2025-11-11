<template>
  <v-card class="chart-card" elevation="0">
    <v-toolbar density="comfortable" color="transparent" class="px-4 pt-3">
      <v-icon class="mr-2">{{ icon }}</v-icon>
      <v-toolbar-title class="font-weight-medium">{{ title }}</v-toolbar-title>
      <v-spacer />
      <v-btn icon variant="text" @click="downloadPng" :title="'Pobierz PNG'">
        <v-icon>mdi-download</v-icon>
      </v-btn>
    </v-toolbar>
    <v-divider />
    <div class="chart-wrap">
      <component :is="componentType" ref="chartRef" :chartData="chartData" :options="computedOptions" />
    </div>
  </v-card>
</template>

<script>
import { BarChart, LineChart, PieChart, DoughnutChart, RadarChart } from 'vue-chart-3'
import { Chart, registerables } from 'chart.js'
Chart.register(...registerables)

export default {
  name: 'ChartCard',
  components: { BarChart, LineChart, PieChart, DoughnutChart, RadarChart },
  props: {
    title: { type: String, default: '' },
    type:  { type: String, default: 'bar' }, // 'bar' | 'line' | 'pie' | 'doughnut' | 'radar'
    chartData: { type: Object, required: true },
    options:   { type: Object, default: () => ({}) }
  },
  computed: {
    componentType () {
      switch (this.type) {
        case 'line': return 'LineChart'
        case 'pie': return 'PieChart'
        case 'doughnut': return 'DoughnutChart'
        case 'radar': return 'RadarChart'
        default: return 'BarChart'
      }
    },
    icon () {
      switch (this.type) {
        case 'line': return 'mdi-chart-line'
        case 'pie': return 'mdi-chart-pie'
        case 'doughnut': return 'mdi-chart-donut'
        case 'radar': return 'mdi-radar'
        default: return 'mdi-chart-bar'
      }
    },
    computedOptions () {
      const nf = new Intl.NumberFormat('pl-PL')
      return {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: { labels: { boxWidth: 14, boxHeight: 14 } },
          tooltip: {
            mode: 'index', intersect: false,
            callbacks: {
              label (ctx) {
                const v = ctx.parsed?.y ?? ctx.parsed
                const label = ctx.dataset?.label ? `${ctx.dataset.label}: ` : ''
                return `${label}${nf.format(v)}`
              }
            }
          }
        },
        scales: (this.type === 'pie' || this.type === 'doughnut') ? {} : {
          x: { grid: { display: false } },
          y: { grid: { color: 'rgba(255,255,255,0.06)' }, ticks: { callback: (v) => nf.format(v) } }
        },
        ...this.options
      }
    }
  },
  methods: {
    downloadPng () {
      const chart = this.$refs.chartRef?.chart
      if (!chart) return
      const url = chart.toBase64Image()
      const a = document.createElement('a')
      a.href = url
      a.download = `${this.title || 'chart'}.png`
      a.click()
    }
  }
}
</script>
