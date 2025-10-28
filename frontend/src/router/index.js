import { createRouter, createWebHistory } from 'vue-router'
import ChartsView from "../views/ChartsView.vue";
// import ChernoffFacesView from '@/views/ChernoffFacesView.vue'


const routes = [
    { path: '/', redirect: '/charts' },
    { path: '/charts', name: 'Charts', component: ChartsView },
    // { path: '/faces', name: 'Faces', component: ChernoffFacesView }
]


export default createRouter({
    history: createWebHistory(),
    routes
})