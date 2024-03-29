package com.project.mygroceryapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.view.marginStart
import androidx.recyclerview.widget.RecyclerView
import com.project.mygroceryapp.model.Cart
import com.project.mygroceryapp.model.Product

import com.google.firebase.firestore.DocumentSnapshot
import com.project.mygroceryapp.R

class ProductAdapter(private val context : Context, private var dataSet : MutableList<DocumentSnapshot>, private val category : String) : RecyclerView.Adapter<ProductAdapter.ViewHolder>(){

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName : TextView
        val productPrice : TextView
        val cardBg : CardView
        val tvAdd : TextView
        val tvQuantity : TextView
        val tvSubtract : TextView
        val ivCart : ImageView

        init {
            productName = itemView.findViewById(R.id.tv_product_name_search)
            productPrice = itemView.findViewById(R.id.tv_item_product_price)
            cardBg = itemView.findViewById(R.id.card_item)
            tvAdd = itemView.findViewById(R.id.tv_add_quantity)
            tvQuantity = itemView.findViewById(R.id.tv_quantity)
            tvSubtract = itemView.findViewById(R.id.tv_subtract_quantity)
            ivCart = itemView.findViewById(R.id.iv_add_to_cart)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_row,parent,false)
        val offset = view.marginStart
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.CENTER
            width = parent.width - offset
            setMargins(10,10,10,10)
        }
        view.layoutParams = params
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if(position % 2 == 0){
            holder.cardBg.setCardBackgroundColor(Color.WHITE)
        }else{
            holder.cardBg.setCardBackgroundColor(Color.GRAY)
        }
        
        //get product info
        val productName = dataSet[position] ["name"].toString()
        val productPrice = dataSet[position] ["price"].toString()
        var quantity = 0

        holder.productName.text = productName
        holder.productPrice.text = productPrice
        holder.tvQuantity.text = quantity.toString()

        holder.tvAdd.setOnClickListener {
            quantity++
            holder.tvQuantity.text = quantity.toString()
        }

        //change quantity
        holder.tvSubtract.setOnClickListener{
            if(quantity > 0 ){
                quantity--
                holder.tvQuantity.text = quantity.toString()
            }
        }

        holder.ivCart.setOnClickListener{
            val name = holder.productName.text.toString()
            val pcs = holder.tvQuantity.text.toString().toInt()
            val price = holder.productPrice.text.toString().toDouble()

            if(pcs > 0){
                val product  =  Product(name, category,  pcs, price)
                Cart.add(product)

                //set quantity of item selected = 0
                holder.tvQuantity.text = 0.toString()
                Toast.makeText(context, "$productName added to list", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, "Invalid Quantity", Toast.LENGTH_LONG).show()
            }
        }
        

    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: MutableList<DocumentSnapshot>){
        dataSet = list
        notifyDataSetChanged()
    }



}