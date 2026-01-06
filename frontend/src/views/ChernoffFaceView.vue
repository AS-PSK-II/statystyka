<template>
  <v-container>
    <div class="display-1 mb-6 page-title">Twarze Chernoffa</div>

    <v-card class="pa-5 fancy-card mb-6" elevation="0">
      <v-row>
        <v-col cols="12" md="8">
          <v-select
              v-model="selectedAttrs"
              :items="airports"
              label="Wybierz atrybuty (2–5 lotnisk)"
              variant="solo-filled"
              rounded="lg"
              multiple
              chips
              closable-chips
              prepend-inner-icon="mdi-airplane"
          />
        </v-col>

        <v-col cols="12" md="4" class="d-flex align-end justify-end">
          <v-btn
              color="primary"
              rounded
              height="44"
              class="mr-3"
              @click="generateFaces"
          >
            <v-icon start>mdi-emoticon-happy-outline</v-icon>
            Generuj Twarz
          </v-btn>
        </v-col>
      </v-row>

      <v-alert type="info" variant="tonal" class="mt-3">
        Każde lotnisko steruje innym elementem twarzy (np. wielkością oczu,
        krzywizną ust, brwiami). Wyższe wartości &rarr; silniejsze „emocje”.
      </v-alert>
    </v-card>

    <!-- LEGENDA ATRYBUTÓW -->
    <v-card
        v-if="face && selectedAttrs.length"
        class="pa-4 mb-6 legend-card"
        elevation="0"
    >
      <div class="text-subtitle-2 font-weight-medium mb-3">
        Legenda: atrybut → element twarzy
      </div>

      <v-row>
        <v-col
            cols="12"
            md="6"
            v-for="(attr, i) in selectedAttrs"
            :key="'legend-' + attr"
            class="d-flex align-center mb-2"
        >
          <v-avatar
              size="22"
              :style="{ backgroundColor: palette[i % palette.length] }"
              class="mr-3"
          ></v-avatar>

          <div>
            <div class="text-body-2 font-weight-medium">{{ attr }}</div>
            <div class="text-caption text-medium-emphasis">
              {{ legendMapping[i] }}
            </div>
          </div>
        </v-col>
      </v-row>
      <v-divider class="my-3" />

      <div class="text-caption text-medium-emphasis mb-2">
        Mapowanie przedziałów kwartylowych na kształty (dla każdego lotniska):
      </div>

      <div class="d-flex flex-wrap">
        <v-chip class="mr-2 mb-2" size="small" variant="outlined">
          0 - Q1 → Kwadrat
        </v-chip>
        <v-chip class="mr-2 mb-2" size="small" variant="outlined">
          Q1 – Q2 → Trójkąt
        </v-chip>
        <v-chip class="mr-2 mb-2" size="small" variant="outlined">
          Q2 – Q3 → Koło
        </v-chip>
        <v-chip class="mr-2 mb-2" size="small" variant="outlined">
          > Q3 → Diament
        </v-chip>
      </div>
    </v-card>

    <v-row v-if="face && isReady">
      <v-col cols="12" md="8" class="mx-auto">
        <v-card class="face-card" elevation="0">
          <div class="pa-3 d-flex justify-space-between align-center">
            <div class="text-subtitle-2 font-weight-medium">
              Twarz Chernoffa
            </div>
          </div>

          <div class="d-flex justify-center pb-2">
            <ChernoffFace :params="face" :size="220" />
          </div>

          <v-divider />

          <div class="pa-3">

            <div class="d-flex flex-wrap">
              <v-chip
                  v-for="(item, i) in summaryRows"
                  :key="item.attr"
                  size="small"
                  :color="palette[i % palette.length]"
                  variant="outlined"
                  class="mr-2 mb-2"
              >
                {{ shortName(item.attr) }}
                • Średnia={{ fmt(item.mean) }}
                • Q1={{ fmt(item.q1) }}
                • Q2={{ fmt(item.q2) }}
                • Q3={{ fmt(item.q3) }}
                • poziom: {{ item.bucketLabel }}
                • kształt: {{ shapeLabel(item.shape) }}
              </v-chip>
            </div>
          </div>
        </v-card>
      </v-col>
    </v-row>

    <v-alert
        v-else
        type="info"
        variant="tonal"
        class="mt-6"
    >
      Wybierz 2–5 lotnisk i kliknij „Generuj Twarz”, aby zobaczyć wizualizację.
    </v-alert>
  </v-container>
</template>

<script>
import ChernoffFace from "@/components/ChernoffFace.vue";

export default {
  name: 'ChernoffFacesView',
  components: { ChernoffFace },
  props: {
    chartData: {
      type: Array,
      required: true
    },
    statisticsData: {
      type: Array,
      required: true
    }
  },
  data () {
    return {
      series: [],
      airports: [],
      selectedAttrs: [],
      face: null,        // jedna twarz
      summaryRows: [],   // tabelka z mean/Q1/Q2/Q3/bucket
      rowValues: [],           // oryginalne wartości wybranych lotnisk per wiersz
      palette: ['#6366F1','#22C1C3','#EF4444','#F59E0B','#10B981','#8B5CF6','#EC4899','#14B8A6'],
      legendMapping: [
        'Steruje kształem oczu',
        'Steruje krzywizną ust (duża wartość → uśmiech, niska → smutek)',
        'Steruje nachyleniem i kształtem brwi (niska wartość → brwi ściągnięte / groźne)',
        'Steruje kształtem uszu',
        'Steruje kształtem nosa'
      ]
    }
  },
  created () {
    this.initFromData()
  },
  watch: {
    chartData: {
      handler () {
        this.initFromData()
      },
      deep: true
    }
  },
  computed: {
    isReady () {
      const c = this.selectedAttrs.length
      return c >= 2 && c <= 5
    }
  },
  methods: {
    bucketToShape(lvl) {
      // 0=kwadrat, 1=trójkąt, 2=koło, 3=diament
      return lvl
    },
    shapeLabel(shape) {
      return ['Kwadrat', 'Trójkąt', 'Koło', 'Diament'][shape] || ''
    },
    initFromData () {
      // kopia i sortowanie rosnące po czasie
      this.face = null
      this.summaryRows = []
      const clone = JSON.parse(JSON.stringify(this.chartData || []))
      this.series = this.sortByTimestamp(clone)
      this.airports = Array.from(
          new Set(this.series.flatMap(s => s.dataPoints.map(d => d.id)))
      )
      this.selectedAttrs = this.airports.slice(0, 3) // domyślnie 3 pierwsze
      this.rowValues = []
    },
    sortByTimestamp(data) {
      const romanMap = { I:1, II:2, III:3, IV:4 };

      return data.sort((a, b) => {
        const [rA, _, yA] = a.timestamp.split(" ");
        const [rB, __, yB] = b.timestamp.split(" ");

        const yearA = Number(yA);
        const yearB = Number(yB);

        const qA = romanMap[rA] || 0;
        const qB = romanMap[rB] || 0;

        if (yearA !== yearB) return yearA - yearB;
        return qA - qB;
      });
    },
    getStat(item, key) {
      const found = item?.values?.find(v => v.name === key)
      return found?.value ?? null
    },
    generateFaces () {
      if (!this.isReady) {
        this.face = null
        this.summaryRows = []
        return
      }

      // bierzemy tylko te lotniska, które user wybrał
      const selected = this.selectedAttrs
          .map(id => this.statisticsData.find(x => x.id === id))
          .filter(Boolean)

      if (!selected.length) {
        this.face = null
        this.summaryRows = []
        return
      }

      const bucketLevel = (avg, q1, q2, q3) => {
        if (avg == null || q1 == null || q2 == null || q3 == null) return 1 // fallback
        if (avg <= q1) return 0
        if (avg <= q2) return 1
        if (avg <= q3) return 2
        return 3
      }

      const bucketLabel = (lvl) => (['≤Q1', 'Q1–Q2', 'Q2–Q3', '>Q3'][lvl] || '')

      // 0..1 z kwartylowego bucketu
      const stats = selected.map(item => {
        const avg = this.getStat(item, 'avg')
        const q1  = this.getStat(item, 'firstQuantile')
        const q2  = this.getStat(item, 'median')          // Q2
        const q3  = this.getStat(item, 'thirdQuantile')   // Q3

        const lvl = bucketLevel(avg, q1, q2, q3)          // 0..3
        const norm = lvl / 3                               // 0..1

        return {
          attr: item.id,
          avg, q1, q2, q3,
          lvl,
          norm,
          bucketLabel: bucketLabel(lvl)
        }
      })

      // do podglądu w chipach
      this.summaryRows = stats.map(s => ({
        attr: s.attr,
        mean: s.avg,
        q1: s.q1,
        q2: s.q2,
        q3: s.q3,
        bucketLabel: s.bucketLabel,
        shape: this.bucketToShape(s.lvl)
      }))

      // mapowanie 2–5 atrybutów -> cechy twarzy
      const vals = stats.map(s => s.norm)
      const lvls = stats.map(s => s.lvl)

      const [v0=0.5,v1=0.5,v2=0.5,v3=0.5,v4=0.5] = vals
      const [l0=2,l1=2,l2=2,l3=2,l4=2] = lvls

      this.face = {
        eyeSize: v0,
        eyeSep: 0.5,
        mouthCurve: v1,
        browTilt: v2,
        noseLen: v4,

        // USZY zamiast kształtu/proporcji głowy
        earSize: v3,                         // 0..1 (z bucketu)
        earShape: this.bucketToShape(l3),     // 0..3

        eyeShape: this.bucketToShape(l0),
        mouthShape: this.bucketToShape(l1),
        browShape: this.bucketToShape(l2),
        noseShape: this.bucketToShape(l4)
      }
    },

    shortName (name) {
      return name.length > 18 ? name.slice(0, 16) + '…' : name
    },

    fmt (n) {
      if (n == null) return '-'
      return new Intl.NumberFormat('pl-PL', { notation: 'compact' }).format(n)
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
.face-card{
  border-radius: 18px;
  background: linear-gradient(180deg, rgba(15,23,42,.9), rgba(15,23,42,.97));
  border: 1px solid rgba(30,64,175,.5);
}
.legend-card {
  border-radius: 18px;
  background: linear-gradient(180deg, rgba(30,41,59,.9), rgba(15,23,42,.95));
  border: 1px solid rgba(148,163,184,.25);
}
</style>
