import Vue from 'vue'
import Router from 'vue-router'
import Button from "../components/Button";
import ButtonDetail from "../components/ButtonDetail";
import Link from "../components/Link";
import Layout from "../components/Layout";
import Container from "../components/Container";
import ContrainerExam from "../components/ContrainerExam";
import Radio from "../components/Radio";
import Checkbox from "../components/Checkbox";
import Input from "../components/Input";
import Select from "../components/Select";
import Switch from "../components/Switchs";
import DatePrickers from "../components/DatePrickers";
import Uploads from "../components/Uploads";
import Form from "../components/Form";
import Messages from "../components/Messages";
import Tables from "../components/Tables";
Vue.use(Router)

export default new Router({
  routes: [
    {path: '/button', component:Button},
    {path: '/buttondetail', component:ButtonDetail},
    {path: '/link', component:Link},
    {path: '/layout', component:Layout},
    {path: '/container', component:Container},
    {path: '/containerexam', component:ContrainerExam},
    {path: '/radio', component:Radio},
    {path: '/checkbox', component:Checkbox},
    {path: '/input', component:Input},
    {path: '/select', component:Select},
    {path: '/switch', component:Switch},
    {path: '/datePickers', component:DatePrickers},
    {path: '/upload', component:Uploads},
    {path: '/form',component:Form},
    {path: '/msg',component:Messages},
    {path: '/table',component:Tables}


  ]
})
