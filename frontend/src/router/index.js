import { createRouter, createWebHistory } from 'vue-router'
import ChartsView from "../views/ChartsView.vue";
import MainView from "@/views/MainView.vue";
// import ChernoffFaceViewsView from '@/views/ChernoffFaceViewsView.vue'


const routes = [
    // { path: '/', redirect: '/charts' },
    { path: '/', name: 'home', component: MainView },
    // { path: '/faces', name: 'Faces', component: ChernoffFaceViewsView }
]


export default createRouter({
    history: createWebHistory(),
    routes
})