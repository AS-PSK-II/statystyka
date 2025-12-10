<template>
  <canvas ref="canvas" :width="size" :height="size" />
</template>

<script>
export default {
  name: 'ChernoffFace',
  props: {
    // wszystkie wartości w [0,1]
    params: {
      type: Object,
      default: () => ({
        eyeSize: 0.5,
        eyeSep: 0.5,
        browTilt: 0.5,
        mouthCurve: 0.5,
        faceW: 0.5,
        faceH: 0.5,
        noseLen: 0.5
      })
    },
    size: {
      type: Number,
      default: 180
    },
    strokeColor: {
      type: String,
      default: '#60A5FA'
    }
  },
  mounted () {
    this.draw()
  },
  watch: {
    params: {
      handler () {
        this.draw()
      },
      deep: true
    }
  },
  methods: {
    draw () {
      const c = this.$refs.canvas
      if (!c) return
      const ctx = c.getContext('2d')
      const s = this.size
      ctx.clearRect(0, 0, s, s)

      ctx.lineWidth = 2
      ctx.strokeStyle = this.strokeColor

      const p = this.params

      const faceW = 0.6 + p.faceW * 0.4
      const faceH = 0.6 + p.faceH * 0.4

      // głowa
      ctx.beginPath()
      ctx.ellipse(
          s / 2,
          s / 2,
          (s * 0.35) * faceW,
          (s * 0.45) * faceH,
          0,
          0,
          Math.PI * 2
      )
      ctx.stroke()

      // oczy
      const eyeSize = 3 + p.eyeSize * 10
      const sep = 15 + p.eyeSep * 25
      const eyeY = s * 0.45
      const eyeX = s / 2

      ctx.beginPath()
      ctx.arc(eyeX - sep, eyeY, eyeSize, 0, Math.PI * 2)
      ctx.stroke()

      ctx.beginPath()
      ctx.arc(eyeX + sep, eyeY, eyeSize, 0, Math.PI * 2)
      ctx.stroke()

      // brwi
      const tilt = (p.browTilt - 0.5) * 1.1 // -0.55..+0.55
      const browY = eyeY - eyeSize - 6

      ctx.beginPath()
      ctx.moveTo(eyeX - sep - eyeSize, browY + tilt * 8)
      ctx.lineTo(eyeX - sep + eyeSize, browY - tilt * 8)
      ctx.stroke()

      ctx.beginPath()
      ctx.moveTo(eyeX + sep - eyeSize, browY - tilt * 8)
      ctx.lineTo(eyeX + sep + eyeSize, browY + tilt * 8)
      ctx.stroke()

      // nos
      const nose = 6 + p.noseLen * 22
      ctx.beginPath()
      ctx.moveTo(eyeX, eyeY)
      ctx.lineTo(eyeX - 4, eyeY + nose * 0.4)
      ctx.lineTo(eyeX, eyeY + nose)
      ctx.stroke()

      // usta
      const mouthCurve = (p.mouthCurve - 0.5) * 1.4 // -0.7..+0.7
      const mw = s * 0.2
      const my = s * 0.68

      ctx.beginPath()
      ctx.moveTo(eyeX - mw, my)
      ctx.quadraticCurveTo(
          eyeX,
          my + mouthCurve * 30,
          eyeX + mw,
          my
      )
      ctx.stroke()
    }
  }
}
</script>
