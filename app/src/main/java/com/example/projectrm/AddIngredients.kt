package com.example.projectrm

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AddIngredients : Fragment() {

    private lateinit var addButton: FloatingActionButton
    private lateinit var generateButton: Button
    private lateinit var ingredientsAdapter: IngredientsAdapter
    private lateinit var ingredientsRV: RecyclerView
    private var ingredientsData = ArrayList<IngredientsModel>()
    private val CAMERA_PERMISSION_CODE = 1001


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_ingredients, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ingredientsRV = view.findViewById(R.id.ingredientsRV)
        ingredientsAdapter = IngredientsAdapter(ingredientsData)
        ingredientsRV.layoutManager = LinearLayoutManager(requireContext())
        ingredientsRV.adapter = ingredientsAdapter
        ingredientsRV.setHasFixedSize(true)

        generateButton = view.findViewById(R.id.generateBtn)
        generateButton.setOnClickListener {

        }

        addButton = view.findViewById(R.id.addButton)
        addButton.setOnClickListener {
            showPopup(addButton)
        }

    }

    private fun showPopup(view: View){
        val popup = PopupMenu(requireContext(), view)
        popup.inflate(R.menu.add_menu)

        popup.setOnMenuItemClickListener { item: MenuItem? ->
            when (item!!.itemId) {
                R.id.camera -> {
                    checkCameraPermission()
                }

                R.id.add -> {
                    addManually()
                }
            }
            true
        }
        popup.show()
    }

    private fun addManually() {
        val view1:View = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_layout, null)
        val ingredientsTextField:EditText = view1.findViewById<EditText>(R.id.ingredientsTextField)
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        with(alertDialogBuilder){
            setTitle("Add Ingredients Manually")
            setMessage("13 ingredients available")
            setView(view1)
            setPositiveButton("Done"){_, _ ->
                val item = ingredientsTextField.text.toString()
                ingredientsData.add(IngredientsModel(item))
                ingredientsAdapter.notifyDataSetChanged()
            }
            setNegativeButton("Cancel") {
                dialog, _ -> dialog.dismiss()
            }
        }
        val builder = alertDialogBuilder.create()
        builder.show()
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        } else {
            openCamera()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Camera permission denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
    }

    companion object {
        private const val CAMERA_REQUEST_CODE = 1002
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val modelInference = AIModel(requireContext())

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            val photo: Bitmap = data?.extras?.get("data") as Bitmap
            val result = modelInference.performInference(photo)
        }
    }

}