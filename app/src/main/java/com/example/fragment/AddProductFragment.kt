package com.example.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AddProductFragment : Fragment(R.layout.fragment_add_product) {

    private lateinit var db: DatabaseHelper
    private var selectedImageUri: Uri? = null
    private lateinit var imgPreview: ImageView

    // Image picker launcher
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            selectedImageUri = result.data?.data
            selectedImageUri?.let {
                imgPreview.setImageURI(it)
                imgPreview.visibility = View.VISIBLE
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = DatabaseHelper(requireContext())

        // Tab switching
        val btnTabAdd = view.findViewById<TextView>(R.id.tabAdd)
        val btnTabManage = view.findViewById<TextView>(R.id.tabManage)
        val addLayout = view.findViewById<View>(R.id.addProductLayout)
        val manageLayout = view.findViewById<View>(R.id.manageProductLayout)

        fun selectTab(isAdd: Boolean) {
            if (isAdd) {
                btnTabAdd.background = requireContext().getDrawable(R.drawable.chip_selected)
                btnTabAdd.setTextColor(resources.getColor(android.R.color.white, null))
                btnTabManage.background = requireContext().getDrawable(R.drawable.chip_unselected)
                btnTabManage.setTextColor(resources.getColor(R.color.yellow, null))
                addLayout.visibility = View.VISIBLE
                manageLayout.visibility = View.GONE
            } else {
                btnTabManage.background = requireContext().getDrawable(R.drawable.chip_selected)
                btnTabManage.setTextColor(resources.getColor(android.R.color.white, null))
                btnTabAdd.background = requireContext().getDrawable(R.drawable.chip_unselected)
                btnTabAdd.setTextColor(resources.getColor(R.color.yellow, null))
                addLayout.visibility = View.GONE
                manageLayout.visibility = View.VISIBLE
                loadManageProducts(view)
            }
        }
        btnTabAdd.setOnClickListener { selectTab(true) }
        btnTabManage.setOnClickListener { selectTab(false) }
        selectTab(true)

        //ADD PRODUCT
        imgPreview = view.findViewById(R.id.imgProductPreview)
        val btnPickImage = view.findViewById<Button>(R.id.btnPickImage)
        val etName = view.findViewById<EditText>(R.id.productName)
        val etDesc = view.findViewById<EditText>(R.id.productDescription)
        val spinner = view.findViewById<Spinner>(R.id.categorySpinner)
        val etPriceS = view.findViewById<EditText>(R.id.priceSmall)
        val etPriceM = view.findViewById<EditText>(R.id.priceMedium)
        val etPriceL = view.findViewById<EditText>(R.id.priceLarge)
        val etPriceReg = view.findViewById<EditText>(R.id.priceRegular)
        val sizePriceLayout = view.findViewById<View>(R.id.sizePriceLayout)
        val regPriceLayout = view.findViewById<View>(R.id.regularPriceLayout)
        val btnAdd = view.findViewById<Button>(R.id.btnAddProduct)
        val btnClear = view.findViewById<Button>(R.id.btnClear)

        val categories = listOf("Fruits", "Pizza", "Soda", "Snacks")
        spinner.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item, categories
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: AdapterView<*>, v: View?, pos: Int, id: Long) {
                val sel = categories[pos]
                val isSized = sel == "Fruits" || sel == "Pizza"
                sizePriceLayout.visibility = if (isSized) View.VISIBLE else View.GONE
                regPriceLayout.visibility = if (isSized) View.GONE else View.VISIBLE
            }

            override fun onNothingSelected(p: AdapterView<*>) {}
        }

        // Image picker
        btnPickImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            imagePickerLauncher.launch(intent)
        }

        // Add product
        btnAdd.setOnClickListener {
            val name = etName.text.toString().trim()
            val desc = etDesc.text.toString().trim()
            val category = spinner.selectedItem.toString()
            val isSized = category == "Fruits" || category == "Pizza"

            if (name.isEmpty()) {
                etName.error = "Name is required"; return@setOnClickListener
            }
            if (desc.isEmpty()) {
                etDesc.error = "Description is required"; return@setOnClickListener
            }

            val (ps, pm, pl, pr) = if (isSized) {
                val s = etPriceS.text.toString().toDoubleOrNull()
                val m = etPriceM.text.toString().toDoubleOrNull()
                val l = etPriceL.text.toString().toDoubleOrNull()
                if (s == null) {
                    etPriceS.error = "Required"; return@setOnClickListener
                }
                if (m == null) {
                    etPriceM.error = "Required"; return@setOnClickListener
                }
                if (l == null) {
                    etPriceL.error = "Required"; return@setOnClickListener
                }
                arrayOf(s, m, l, 0.0)
            } else {
                val r = etPriceReg.text.toString().toDoubleOrNull()
                if (r == null) {
                    etPriceReg.error = "Required"; return@setOnClickListener
                }
                arrayOf(0.0, 0.0, 0.0, r)
            }

            // Save image path or default
            val imageName = selectedImageUri?.toString() ?: "baseline_fastfood_24"

            val newItem = MenuItemData(
                name = name,
                description = desc,
                category = category,
                priceS = ps, priceM = pm, priceL = pl, priceReg = pr,
                imageName = imageName
            )
            val rowId = db.addMenuItem(newItem)

            if (rowId > 0) {
                Toast.makeText(
                    requireContext(),
                    "✅ '$name' added to menu!", Toast.LENGTH_SHORT
                ).show()
                clearForm(etName, etDesc, etPriceS, etPriceM, etPriceL, etPriceReg)
                imgPreview.visibility = View.GONE
                selectedImageUri = null
            } else {
                Toast.makeText(requireContext(), "Failed to add product.", Toast.LENGTH_SHORT)
                    .show()
            }
        }

        btnClear.setOnClickListener {
            clearForm(etName, etDesc, etPriceS, etPriceM, etPriceL, etPriceReg)
            imgPreview.visibility = View.GONE
            selectedImageUri = null
        }
    }

    // MANAGE PRODUCTS (with Delete)
    private fun loadManageProducts(view: View) {
        val recycler = view.findViewById<RecyclerView>(R.id.manageProductsRecycler)
        val tvEmpty = view.findViewById<TextView>(R.id.tvManageEmpty)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        val items = db.getAllMenuItemsAdmin().toMutableList()

        if (items.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
            recycler.visibility = View.GONE
            return
        }
        tvEmpty.visibility = View.GONE
        recycler.visibility = View.VISIBLE

        recycler.adapter = ManageProductAdapter(items) { item, position ->
            val builder =
                com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                    .setTitle("🗑 Delete Product?")
                    .setMessage("Are you sure you want to remove \"${item.name}\"?\nThis action cannot be undone.")

                    // FIRST OPTION: Cancel (Left Side)
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }

                    // SECOND OPTION: Delete (Right Side)
                    .setPositiveButton("Delete") { _, _ ->
                        db.hardDeleteMenuItem(item.id)
                        items.removeAt(position)
                        recycler.adapter?.notifyItemRemoved(position)

                        if (items.isEmpty()) {
                            tvEmpty.visibility = View.VISIBLE
                            recycler.visibility = View.GONE
                        }

                        com.google.android.material.snackbar.Snackbar.make(
                            requireView(),
                            "'${item.name}' deleted",
                            com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
                        ).show()
                    }

            val dialog = builder.create()
            dialog.show()

            dialog.getButton(android.app.AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(android.graphics.Color.GRAY)
            dialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE)
                .setTextColor(android.graphics.Color.RED)
        }
    }

    private fun clearForm(vararg fields: EditText) = fields.forEach { it.text.clear() }

}