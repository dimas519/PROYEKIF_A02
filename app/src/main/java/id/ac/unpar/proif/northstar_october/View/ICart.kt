package id.ac.unpar.proif.northstar_october.View

import id.ac.unpar.proif.northstar_october.Model.Box

interface ICart {
    fun loadCart (cart: ArrayList<Box>)
    fun changeTotal (value: String, count: Int)
    fun checkUncheckAll(numSelected :Int)
}