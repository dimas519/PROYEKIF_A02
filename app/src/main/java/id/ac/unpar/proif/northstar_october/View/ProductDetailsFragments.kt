package id.ac.unpar.proif.northstar_october.View

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import id.ac.unpar.proif.northstar_october.Model.Code
import id.ac.unpar.proif.northstar_october.Model.Product
import id.ac.unpar.proif.northstar_october.databinding.FragmentProductDetailsBinding
import org.parceler.Parcels

class ProductDetailsFragments: Fragment(), View.OnClickListener, View.OnTouchListener {

    lateinit var binding: FragmentProductDetailsBinding
    private var xCoordinateFrom = 0f
    private var xCoordinateAfter = 0f
    private var curPic = 0
    lateinit var curProduct: Product
    private var pageFrom: Int = Code.PAGE_LIST_MODE

    companion object {
        fun newInstance(): ProductDetailsFragments {
            return ProductDetailsFragments()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentProductDetailsBinding.inflate(inflater, container, false)

        // from fragment list and tiles listener
        parentFragmentManager.setFragmentResultListener(
            "MOVE_DETAILS", this
        ) { requestKey, result ->
            pageFrom = result.getInt("pageFrom")
            val product = Parcels.unwrap<Any>(result.getParcelable("products")) as Product
            setProducts(product)
        }

        // set onclick listener
        binding.btnAdd.setOnClickListener { view: View -> onClick(view) }
        binding.ivBack.setOnClickListener { view: View -> onClick(view) }
        binding.ivPics.setOnTouchListener { view: View, motionEvent: MotionEvent ->onTouch(view,motionEvent)}
        return binding.root
    }

    fun setProducts(product: Product) {
        // set all products info here
        binding.tvName.text = product.name
        binding.tvCategory.text = product.getCategory()
        binding.tvCondition.text = product.getCondition()
        binding.tvPrice.text = product.price.toString() + ""
        binding.tvDesc.text = product.description
        curProduct = product

        //IMPLEMENTASI GLIDE LIBRARY
        Glide.with(requireActivity())
            .load(
                requireActivity().resources.getIdentifier(
                    product.photos[0],
                    "drawable",
                    requireActivity().packageName
                )
            )
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(binding.ivPics)
    }

    override fun onClick(view: View) {
        if (view === binding.btnAdd) {
            addCartToast()
        } else if (view === binding.ivBack) {
            changePage(pageFrom)
        }
    }

    private fun addCartToast() {
        val toast = Toast.makeText(context, "Product added to cart", Toast.LENGTH_SHORT)
        toast.show()
    }

    //METHOD GANTI HALAMAN
    private fun changePage(page: Int) {
        val result = Bundle()
        result.putInt("page", page)
        parentFragmentManager.setFragmentResult("CHANGE_PAGE", result)
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                xCoordinateFrom = motionEvent.x
                Log.d("DOWN", xCoordinateFrom.toString() + "")
            }
            MotionEvent.ACTION_UP -> {
                xCoordinateAfter = motionEvent.x
                Log.d("UP", xCoordinateAfter.toString() + "")
                if (Math.abs(xCoordinateAfter - xCoordinateFrom) > 150) {
                    if (xCoordinateAfter > xCoordinateFrom) {
                        // swipe right
                        curPic++
                        if (curPic > 2) {
                            curPic = 0
                        }
                    } else {
                        // swipe left
                        curPic--
                        if (curPic < 0) {
                            curPic = 2
                        }
                    }
                    slidePic()
                }
            }
        }
        return true
    }

    private fun slidePic() {
        Glide.with(requireActivity())
            .load(
                requireActivity().resources.getIdentifier(
                    curProduct.photos[curPic],
                    "drawable",
                    requireActivity().packageName
                )
            )
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(binding.ivPics)
    }
}