package com.project.mygroceryapp.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.project.mygroceryapp.model.Cart
import com.project.mygroceryapp.model.Product
import com.project.mygroceryapp.R
import java.math.RoundingMode
import java.text.DecimalFormat

class CartAdapter(private val context : Context, private val orderList : MutableList<Product>, private var itemClickListener: OnItemClickListener) : RecyclerView.Adapter<CartAdapter.ViewHolder>(){

    interface OnItemClickListener {
        fun onItemClick(total: Double)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName : TextView
        val productPrice : TextView
        val quantity : TextView
        val amount : TextView
        val btnRemove : ImageView
        val btnAdd : TextView
        val btnSubtract : TextView
        val card : CardView

        init {
            productName = itemView.findViewById(R.id.tv_product_name_cart)
            productPrice = itemView.findViewById(R.id.tv_item_product_price_cart)
            quantity = itemView.findViewById(R.id.tv_quantity_cart)
            amount = itemView.findViewById(R.id.tv_amount_cart)
            btnRemove = itemView.findViewById(R.id.iv_remove)
            btnAdd = itemView.findViewById(R.id.tv_add_quantity_cart)
            btnSubtract = itemView.findViewById(R.id.tv_subtract_quantity_cart)
            card = itemView.findViewById(R.id.card_cart_item)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        //change bg of card
        if (position % 2 == 0){
            holder.card.setCardBackgroundColor(Color.WHITE)
        }else{
            holder.card.setCardBackgroundColor(Color.GRAY)
        }
        val currentProduct = orderList[position]
        val name = currentProduct.productName
        val price = currentProduct.productPrice
        val quantity = currentProduct.productQuantity

        holder.productName.text = name
        holder.productPrice.text = price.toString()
        holder.quantity.text = quantity.toString()
        holder.amount.text = "Total: ${roundOffDecimal(price * quantity)}"


        //controls
        holder.btnAdd.setOnClickListener {
            //get current quantity
            val currentQuantity = holder.quantity.text.toString().toInt()

            if(currentQuantity <= 99){
                currentProduct.productQuantity++
                //update value of quantity
                updateViews(holder, currentProduct, price)

            }else{
                Toast.makeText(context, "Quantity must be 1-99 only", Toast.LENGTH_SHORT).show()
            }

        }

        holder.btnSubtract.setOnClickListener {
            //get current quantity
            val currentQuantity = holder.quantity.text.toString().toInt()

            if(currentQuantity > 1){
                currentProduct.productQuantity--
                //update value of quantity
                updateViews(holder, currentProduct, price)
            }else{
                Toast.makeText(context, "Quantity must be 1-99 only", Toast.LENGTH_SHORT).show()
            }
        }

        holder.btnRemove.setOnClickListener {
            val list = Cart.getList()
            list.remove(currentProduct)
            notifyItemRemoved(position)
            updateViews(holder, currentProduct, price)
        }

    }

    private fun updateViews(
        holder: ViewHolder,
        currentProduct: Product,
        price: Double
    ) {
        holder.quantity.text = currentProduct.productQuantity.toString()
        holder.amount.text = "Total: ${roundOffDecimal(currentProduct.productQuantity * price)}"
        //update total
        itemClickListener.onItemClick(roundOffDecimal(Cart.getTotal()))
    }

    override fun getItemCount(): Int = orderList.size

    private fun roundOffDecimal(number: Double): Double {
        val df = DecimalFormat("#.##")
        df.roundingMode = RoundingMode.FLOOR
        return df.format(number).toDouble()
    }
}


