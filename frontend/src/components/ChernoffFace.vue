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
        noseLen: 0.5,
        earSize: 0.5,
        earShape: 1,
        eyeShape: 2,
        browShape: 2,
        noseShape: 2,
        mouthShape: 2
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
    drawShape(ctx, shape, x, y, size) {
      // shape: 0=kwadrat, 1=trójkąt, 2=koło, 3=diament
      ctx.beginPath()
      if (shape === 0) { // square
        ctx.rect(x - size, y - size, size * 2, size * 2)
      } else if (shape === 1) { // triangle
        ctx.moveTo(x, y - size)
        ctx.lineTo(x + size, y + size)
        ctx.lineTo(x - size, y + size)
        ctx.closePath()
      } else if (shape === 2) { // circle
        ctx.arc(x, y, size, 0, Math.PI * 2)
      } else { // diamond
        ctx.moveTo(x, y - size)
        ctx.lineTo(x + size, y)
        ctx.lineTo(x, y + size)
        ctx.lineTo(x - size, y)
        ctx.closePath()
      }
      ctx.stroke()
    },
    draw () {
      const c = this.$refs.canvas
      if (!c) return
      const ctx = c.getContext('2d')
      const s = this.size
      ctx.clearRect(0, 0, s, s)

      ctx.lineWidth = 2
      ctx.strokeStyle = this.strokeColor

      const p = this.params

      const faceW = 0.80
      const faceH = 0.78

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

      // USZY (rozmiar + kształt)
      const ear = 6 + (p.earSize ?? 0.5) * 10 // 6..16
      const earY = s * 0.52

      // pozycje uszu przy krawędzi głowy
      const leftEarX  = s * 0.16
      const rightEarX = s * 0.84

      this.drawShape(ctx, p.earShape ?? 2, leftEarX, earY, ear)
      this.drawShape(ctx, p.earShape ?? 2, rightEarX, earY, ear)

      // oczy
      const eyeSize = 3 + p.eyeSize * 10
      const sep = 15 + p.eyeSep * 25
      const eyeY = s * 0.45
      const eyeX = s / 2

      this.drawShape(ctx, p.eyeShape, eyeX - sep, eyeY, eyeSize)
      this.drawShape(ctx, p.eyeShape, eyeX + sep, eyeY, eyeSize)

      // brwi
      const tilt = (p.browTilt - 0.5) * 1.1
      const browY = eyeY - eyeSize - 6

      ctx.beginPath()
      ctx.moveTo(eyeX - sep - eyeSize, browY + tilt * 8)
      ctx.lineTo(eyeX - sep + eyeSize, browY - tilt * 8)
      ctx.stroke()

      ctx.beginPath()
      ctx.moveTo(eyeX + sep - eyeSize, browY - tilt * 8)
      ctx.lineTo(eyeX + sep + eyeSize, browY + tilt * 8)
      ctx.stroke()

      this.drawShape(ctx, p.browShape, eyeX - sep, browY - 10, 5)
      this.drawShape(ctx, p.browShape, eyeX + sep, browY - 10, 5)

      // nos (kształt + długość)
      const nose = 6 + p.noseLen * 22
      const nx = eyeX
      const ny = eyeY + nose * 0.75
      this.drawShape(ctx, p.noseShape, nx, ny, 6)

    // usta
      const mouthCurve = (p.mouthCurve - 0.5) * 1.4
      const mw = s * 0.2
      const my = s * 0.68

      ctx.beginPath()
      ctx.moveTo(eyeX - mw, my)
      ctx.quadraticCurveTo(eyeX, my + mouthCurve * 30, eyeX + mw, my)
      ctx.stroke()

      // znacznik kształtu ust (czytelny bucket)
      this.drawShape(ctx, p.mouthShape, eyeX, my + 12, 6)
    }
  }
}
</script>
