package id.ac.unpar.proif.northstar_october

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import id.ac.unpar.proif.northstar_october.Model.Code
import id.ac.unpar.proif.northstar_october.Model.Inventory
import id.ac.unpar.proif.northstar_october.View.*
import id.ac.unpar.proif.northstar_october.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding:ActivityMainBinding
    lateinit var fm: FragmentManager
    lateinit var ft: FragmentTransaction
    lateinit var fragments: Array<Fragment>
    lateinit var productListFragments: ProductListFragments
    lateinit var productTilesFragments: ProductTilesFragments
    lateinit var productDetailsFragments: ProductDetailsFragments
    lateinit var productCartFragments: ProductCartFragments
    lateinit var paymentFragment: PaymentFragment
    lateinit var addressFragment: AddressFragment
    lateinit var inv: Inventory
    private var currentFragment = 0
    private var backPointer = intArrayOf(
        Code.PAGE_EXIT,
        Code.PAGE_LIST_MODE,
        Code.PAGE_LIST_MODE,
        Code.PAGE_LIST_MODE,
        Code.PAGE_CART,
        Code.PAGE_PAYMENT
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(this.binding.root)

        supportActionBar?.hide()

        // Make new inventory
        this.inv = Inventory()

        // Fragment initiation
        this.fm = this.supportFragmentManager
        this.productCartFragments = ProductCartFragments.newInstance()
        this.productListFragments = ProductListFragments.newInstance(this.inv)
        this.productTilesFragments = ProductTilesFragments.newInstance(this.inv)
        this.productDetailsFragments = ProductDetailsFragments.newInstance()

        this.paymentFragment = PaymentFragment.newInstance()
        this.addressFragment = AddressFragment.newInstance()

        fragments = arrayOf(
            this.productListFragments,
            this.productTilesFragments,
            this.productDetailsFragments,
            this.productCartFragments,
            this.paymentFragment,
            this.addressFragment
        )

        // Add product-list-fragment to fragment transaction
        val ft = this.fm.beginTransaction()
        ft.add(R.id.fragment_container, this.productListFragments)
            .add(R.id.fragment_container,this.productCartFragments)
            .hide(this.productCartFragments)
            .addToBackStack(null).commit()
        currentFragment = Code.PAGE_LIST_MODE

        // changePage listener
        this.supportFragmentManager.setFragmentResultListener(
            Code.REQKEY_CHANGE_PAGE, this
        ) { requestKey, result ->
            val page = result.getInt("page")
            changePage(page)
        }
    }

    private fun changePage (page: Int) {
        when (page) {
            Code.PAGE_EXIT -> {
                this.closeApplication()
            }
            else -> {
                ft = fm.beginTransaction()
                ft.hide(fragments[currentFragment])

                if (fragments[page].isAdded) {
                    ft.show(fragments[page])
                }
                else {
                    ft.add(R.id.fragment_container, fragments[page])
                }

                ft.commit()
                currentFragment = page
            }
        }
    }
    private fun closeApplication () {
        moveTaskToBack(true)
        finish()
    }

    override fun onBackPressed() {
        changePage(backPointer[currentFragment])
    }
}