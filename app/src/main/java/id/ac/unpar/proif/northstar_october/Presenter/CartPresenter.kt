package id.ac.unpar.proif.northstar_october.Presenter

import id.ac.unpar.proif.northstar_october.Model.Product
import id.ac.unpar.proif.northstar_october.Model.Box
import id.ac.unpar.proif.northstar_october.Model.Cart
import id.ac.unpar.proif.northstar_october.View.ICart

class CartPresenter (private val ui: ICart) {
    private var cart: ArrayList<Box> = ArrayList()

    fun addProduct (product: Product) {
        val size = cart.size
        var check = false
        for (i in 0 until size) {
            if (cart[i].getProduct() == product) {
                cart[i].addQuantity(1)
                check = true
            }
        }
        when (check) {
            false -> cart.add(Box(product))
        }
        this.ui.checkUncheckAll(cart.size)
        setTotalCost()
        ui.loadCart(cart)
    }

    fun toogleCheckBox (box: Box) {
        val idx = cart.indexOf(box)
        cart[idx].toogleChecked()

        this.ui.checkUncheckAll(cart.size)
        setTotalCost()
    }

    fun setTotalCost () {
        val len = cart.size
        var total = 0
        var count = 0
        for (i in 0 until len) {
            if (cart[i].getIsChecked()) {
                total += cart[i].getTotalPrice()
                count += 1
            }
        }
        val formattedTotal = "$ $total"
        ui.changeTotal(formattedTotal, count)
    }

    fun deleteProduct (box: Box) {
        cart.remove(box)
        ui.loadCart(cart)
    }

    fun changeQuantity (box: Box, value: Int) {
        val idx = cart.indexOf(box)
        cart[idx].addQuantity(value)
        ui.loadCart(cart)
    }

    fun getAllItemSelected (): Cart {
        val len = cart.size
        val cart = Cart()
        for (i in 0 until len) {
            if (this.cart[i].getIsChecked()) {
                cart.addBoxToCart(this.cart[i])
            }
        }
        return cart
    }

    fun checkOrUncheckAllitems(checkAll:Boolean){
        cart.forEach{
            if(checkAll){ //kalau mau check semua
                if(!it.getIsChecked()){
                    it.toogleChecked()
                }
            }else{
                if(it.getIsChecked()){
                    it.toogleChecked()
                }
            }

        }
        setTotalCost()
        ui.loadCart(cart)
    }





}