// /plugins/viewer.js

import Vue from 'vue';
import Viewer from 'v-viewer'
import 'viewerjs/dist/viewer.css'

Vue.use(Viewer, {
    defaultOptions: {
        button: true,
        navbar: false,
        title: false,
        toolbar: true,
    }
})
