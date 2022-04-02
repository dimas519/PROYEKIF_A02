package id.ac.unpar.proif.northstar_october.View

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import id.ac.unpar.proif.northstar_october.Model.Box
import id.ac.unpar.proif.northstar_october.Model.Code
import id.ac.unpar.proif.northstar_october.Model.Inventory
import id.ac.unpar.proif.northstar_october.Model.Product
import id.ac.unpar.proif.northstar_october.Presenter.CartPresenter
import id.ac.unpar.proif.northstar_october.databinding.FragmentCartBinding
import org.parceler.Parcels

class ProductCartFragments: Fragment(), View.OnClickListener, ICart {

    private lateinit var binding: FragmentCartBinding
    private lateinit var presenter: CartPresenter
    private lateinit var adapter: ProductCartAdapter
    private var pageFrom: Int = Code.PAGE_LIST_MODE
    private var countItemSelected: Int = 0
    private var checkAll:Boolean=false

    companion object {
        // singleton
        fun newInstance (): ProductCartFragments {
            return ProductCartFragments()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentCartBinding.inflate(inflater, container, false)

        // inisiasi
        presenter=CartPresenter(this)
        adapter = ProductCartAdapter(requireActivity(), presenter, parentFragmentManager)

        // set adapter
        binding.lvCart.adapter = adapter

        // result listener
        parentFragmentManager.setFragmentResultListener(
            Code.REQKEY_ADD_PRODUCT_TO_CART,this
        ) { requestKey, result ->
            val product = Parcels.unwrap<Any>(result.getParcelable("product")) as Product
            addItemFromDetails(product)
        }
        parentFragmentManager.setFragmentResultListener(
            Code.REQKEY_PAGE_FROM, this
        ) { requestKey, result ->
            pageFrom = result.getInt("pageFrom")
        }
        parentFragmentManager.setFragmentResultListener(
            Code.REQKEY_REMOVE_BOX_AT_CART, this
        ) { requestKey, result ->
            val box = Parcels.unwrap<Any>(result.getParcelable("box")) as Box
            presenter.deleteProduct(box)
            presenter.setTotalCost()
        }

        // set binding textView text
        binding.total.text = "$ 0"

        // set click listener
        binding.ivBack.setOnClickListener(this::onClick)
        binding.checkout.setOnClickListener(this::onClick)
        binding.checkbox.setOnClickListener(this)

        return binding.root
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.ivBack -> {
                changePage(pageFrom)
            }
            binding.checkout -> {
                moveToPayment()
            }binding.checkbox->{
                this.selectUnselectAllItem()
            }
        }
    }

    override fun loadCart(cart: ArrayList<Box>) {
        adapter.loadData(cart)
    }

    override fun changeTotal(value: String, count: Int) {
        binding.total.text = value
        binding.checkout.text = "Purchase ($count)"
        countItemSelected = count
    }

    override fun checkUncheckAll(numSelected :Int) {
        this.checkAll=numSelected-1==countItemSelected;
            this.binding.checkbox.isChecked =this.checkAll
            this.binding.checkbox.isChecked =this.checkAll
    }

    private fun addItemFromDetails (product: Product) {
        presenter.addProduct(product)
    }

    private fun changePage (page: Int) {
        val result = Bundle()
        result.putInt("page", page)
        parentFragmentManager.setFragmentResult(Code.REQKEY_CHANGE_PAGE, result)
    }

    private fun moveToPayment () {
        if (countItemSelected > 0) {
            val cart = presenter.getAllItemSelected()
            val result = Bundle()
            result.putParcelable("cart", Parcels.wrap(cart))
            parentFragmentManager.setFragmentResult(Code.REQKEY_MOVE_TO_PAYMENT, result)
            changePage(Code.PAGE_PAYMENT)
        }else{
            noItemSelectedToast()
        }
    }

    private fun noItemSelectedToast() {
        val toast = Toast.makeText(context, "Please select an item", Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()

    }

    private fun selectUnselectAllItem(){
        this.checkAll=!checkAll
        this.presenter.checkOrUncheckAllitems(this.checkAll);
    }

}