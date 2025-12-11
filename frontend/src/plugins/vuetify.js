import { createVuetify } from 'vuetify'
import { pl } from 'vuetify/locale'


export default createVuetify({
    theme: {
        defaultTheme: 'dark'
    },
    locale: {
        locale: 'pl',
        fallback: 'pl',
        messages: { pl },
    },
})