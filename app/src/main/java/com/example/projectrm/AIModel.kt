//package com.example.projectrm
//
//import android.content.Context
//import android.graphics.Bitmap
//import org.pytorch.IValue
//import org.pytorch.Module
//import org.pytorch.torchvision.TensorImageUtils
//
//class AIModel(private val context: Context) {
//    private val modelPath = "file:///android_asset/best.pt"
//    private val module = Module.load(modelPath)
//
//    fun performInference(bitmap: Bitmap): FloatArray {
//
//        val inputTensor = TensorImageUtils.bitmapToFloat32Tensor(bitmap, floatArrayOf(0.485f, 0.456f, 0.406f), floatArrayOf(0.229f, 0.224f, 0.225f))
//
//        val outputTensor = module.forward(IValue.from(inputTensor)).toTensor()
//
//        val outputArray = outputTensor.dataAsFloatArray
//
//        return outputArray
//    }
//}