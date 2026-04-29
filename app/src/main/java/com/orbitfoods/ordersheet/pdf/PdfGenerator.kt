package com.orbitfoods.ordersheet.pdf

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import com.orbitfoods.ordersheet.models.Order
import java.io.File
import java.io.FileOutputStream

class PdfGenerator(private val context: Context) {

    // Colors
    private val colorHeader = Color.parseColor("#5A939E")
    private val colorText = Color.parseColor("#333333")
    private val colorWhite = Color.WHITE
    private val colorLightGray = Color.parseColor("#F5F5F5")
    private val colorGray = Color.parseColor("#CCCCCC")
    private val colorBg = Color.parseColor("#FAF8F5")

    // Page A4
    private val pageWidth = 595
    private val pageHeight = 842
    private val margin = 30f

    // Table column X positions
    private val col1 = 35f
    private val col2 = 80f
    private val col3 = 280f
    private val col4 = 360f
    private val col5 = 450f

    private fun getBanglaFont(): Typeface {
        return try {
            Typeface.createFromAsset(context.assets, "solaiman_lipi.ttf")
        } catch (e: Exception) {
            Typeface.DEFAULT
        }
    }

    private fun getBoldFont(): Typeface {
        return try {
            val base = Typeface.createFromAsset(context.assets, "solaiman_lipi.ttf")
            Typeface.create(base, Typeface.BOLD)
        } catch (e: Exception) {
            Typeface.DEFAULT_BOLD
        }
    }

    // High quality bitmap loader from assets
    private fun loadBitmapFromAssets(fileName: String): Bitmap? {
        return try {
            val stream = context.assets.open(fileName)
            val options = BitmapFactory.Options().apply {
                inPreferredConfig = Bitmap.Config.ARGB_8888  // highest quality
            }
            val bitmap = BitmapFactory.decodeStream(stream, null, options)
            stream.close()
            bitmap
        } catch (e: Exception) {
            null
        }
    }

    // High quality bitmap scaler
    private fun scaleBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        val scaleX = width.toFloat() / bitmap.width.toFloat()
        val scaleY = height.toFloat() / bitmap.height.toFloat()
        val matrix = Matrix()
        matrix.postScale(scaleX, scaleY)
        return Bitmap.createBitmap(
            bitmap, 0, 0,
            bitmap.width, bitmap.height,
            matrix, true
        )
    }

    // High quality paint for drawing bitmaps
    private fun getImagePaint(): Paint {
        return Paint().apply {
            isAntiAlias = true
            isFilterBitmap = true
            isDither = true
        }
    }

    fun generatePdf(order: Order, isOfficeCopy: Boolean): File {

        val copyType = if (isOfficeCopy) "Office" else "Customer"
        val safeDateName = order.date.replace("/", "-")
        val fileName = "${copyType}_${order.serialNumber}_${safeDateName}.pdf"
        val file = File(context.getExternalFilesDir(null), fileName)

        val banglaFont = getBanglaFont()
        val boldFont = getBoldFont()
        val imagePaint = getImagePaint()

        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        // Background
        val bgPaint = Paint().apply { color = colorBg }
        canvas.drawRect(0f, 0f, pageWidth.toFloat(), pageHeight.toFloat(), bgPaint)

        var yPos = margin

        // ══════════════════════════════════════════════════
        // TOP SECTION — Orbit Logo LEFT + ORBIT FOODS text
        // ══════════════════════════════════════════════════
        val orbitLogo = loadBitmapFromAssets("logo_eng.png")
        if (orbitLogo != null) {
            val logoWidth = 130
            val logoHeight = 65
            val scaledLogo = scaleBitmap(orbitLogo, logoWidth, logoHeight)
            canvas.drawBitmap(scaledLogo, margin, yPos + 8f, imagePaint)

            // ORBIT FOODS text to the right of logo
            val orbitTitlePaint = Paint().apply {
                color = colorHeader
                textSize = 30f
                typeface = boldFont
                isAntiAlias = true
                letterSpacing = 0.12f
            }
            canvas.drawText(
                "ORBIT FOODS",
                margin + logoWidth + 18f,
                yPos + 42f,
                orbitTitlePaint
            )

            val orbitSubPaint = Paint().apply {
                color = colorText
                textSize = 11f
                typeface = banglaFont
                isAntiAlias = true
            }
            canvas.drawText(
                "বাজার চৌয়ারা, কুমিল্লা",
                margin + logoWidth + 18f,
                yPos + 60f,
                orbitSubPaint
            )

            yPos += logoHeight + 18f

        } else {
            // Fallback if logo file missing
            val fallbackPaint = Paint().apply {
                color = colorHeader
                textSize = 30f
                typeface = boldFont
                isAntiAlias = true
            }
            canvas.drawText("ORBIT FOODS", margin, yPos + 35f, fallbackPaint)
            yPos += 55f
        }

        // Teal divider line under top section
        val tealLinePaint = Paint().apply {
            color = colorHeader
            strokeWidth = 2.5f
        }
        canvas.drawLine(margin, yPos, pageWidth - margin, yPos, tealLinePaint)
        yPos += 14f

        // ══════════════════════════════════════════════════
        // THREE COLUMN HEADER SECTION
        // Left  : bKash + Nagad (side by side) + phones
        // Center: অরবিট ফুডস + address
        // Right : ক্যাশ মেমো
        // ══════════════════════════════════════════════════
        val headerStartY = yPos

        // ── LEFT COLUMN ───────────────────────────────────
        val payLogoWidth = 68
        val payLogoHeight = 32

        // bKash logo
        val bkashBitmap = loadBitmapFromAssets("bkash_logo.png")
        if (bkashBitmap != null) {
            val scaled = scaleBitmap(bkashBitmap, payLogoWidth, payLogoHeight)
            canvas.drawBitmap(scaled, margin, headerStartY, imagePaint)
        } else {
            val bkashPaint = Paint().apply {
                color = Color.parseColor("#E2136E")
                textSize = 13f
                typeface = boldFont
                isAntiAlias = true
            }
            canvas.drawText("bKash", margin, headerStartY + 22f, bkashPaint)
        }

        // Nagad logo — placed RIGHT NEXT to bKash (8f gap)
        val nagadStartX = margin + payLogoWidth + 8f
        val nagadBitmap = loadBitmapFromAssets("nagad_logo.png")
        if (nagadBitmap != null) {
            val scaled = scaleBitmap(nagadBitmap, payLogoWidth, payLogoHeight)
            canvas.drawBitmap(scaled, nagadStartX, headerStartY, imagePaint)
        } else {
            val nagadPaint = Paint().apply {
                color = Color.parseColor("#F6821F")
                textSize = 13f
                typeface = boldFont
                isAntiAlias = true
            }
            canvas.drawText("Nagad", nagadStartX, headerStartY + 22f, nagadPaint)
        }

        // Phone numbers below both logos with clear gap
        val phonePaint = Paint().apply {
            color = colorText
            textSize = 11f
            typeface = banglaFont
            isAntiAlias = true
        }
        val phoneY = headerStartY + payLogoHeight + 10f
        canvas.drawText("01615535770", margin, phoneY + 14f, phonePaint)
        canvas.drawText("01858472223", margin, phoneY + 28f, phonePaint)

        // ── CENTER COLUMN ─────────────────────────────────
        val centerX = pageWidth / 2f

        val banglaNamePaint = Paint().apply {
            color = colorText
            textSize = 22f
            typeface = boldFont
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText("অরবিট ফুডস", centerX, headerStartY + 26f, banglaNamePaint)

        val banglaAddrPaint = Paint().apply {
            color = colorText
            textSize = 12f
            typeface = banglaFont
            isAntiAlias = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(
            "বাজার চৌয়ারা, কুমিল্লা",
            centerX,
            headerStartY + 44f,
            banglaAddrPaint
        )

        // ── RIGHT COLUMN ──────────────────────────────────
        val rightX = pageWidth - margin

        val cashMemoPaint = Paint().apply {
            color = colorText
            textSize = 16f
            typeface = boldFont
            isAntiAlias = true
            textAlign = Paint.Align.RIGHT
        }
        canvas.drawText("ক্যাশ মেমো", rightX, headerStartY + 26f, cashMemoPaint)

        if (isOfficeCopy) {
            val officePaint = Paint().apply {
                color = Color.parseColor("#C0392B")
                textSize = 12f
                typeface = boldFont
                isAntiAlias = true
                textAlign = Paint.Align.RIGHT
            }
            canvas.drawText("Office Copy", rightX, headerStartY + 46f, officePaint)
        }

        // Move yPos below header section with EXTRA GAP
        yPos = headerStartY + payLogoHeight + 55f

        // Gray divider
        val grayLinePaint = Paint().apply {
            color = colorGray
            strokeWidth = 1f
        }
        canvas.drawLine(margin, yPos, pageWidth - margin, yPos, grayLinePaint)
        yPos += 18f  // extra gap after divider before order info

        // ══════════════════════════════════════════════════
        // ORDER INFO SECTION
        // ══════════════════════════════════════════════════
        val infoPaint = Paint().apply {
            color = colorText
            textSize = 13f
            typeface = banglaFont
            isAntiAlias = true
        }
        canvas.drawText("ক্রমিক নং: ${order.serialNumber}", margin, yPos, infoPaint)
        canvas.drawText("তারিখ: ${order.date}", pageWidth / 2f, yPos, infoPaint)
        yPos += 22f
        canvas.drawText("নাম: ${order.customerName}", margin, yPos, infoPaint)
        canvas.drawText("ঠিকানা: ${order.address}", pageWidth / 2f, yPos, infoPaint)
        yPos += 22f

        canvas.drawLine(margin, yPos, pageWidth - margin, yPos, grayLinePaint)
        yPos += 15f

        // ══════════════════════════════════════════════════
        // PRODUCT TABLE
        // ══════════════════════════════════════════════════

        // Table header background
        val tableHeaderBg = Paint().apply { color = colorHeader }
        canvas.drawRect(margin, yPos, pageWidth - margin, yPos + 30f, tableHeaderBg)

        val tableHeaderPaint = Paint().apply {
            color = colorWhite
            textSize = 13f
            typeface = boldFont
            isAntiAlias = true
        }
        canvas.drawText("ক্রম", col1, yPos + 21f, tableHeaderPaint)
        canvas.drawText("পণ্যের নাম", col2, yPos + 21f, tableHeaderPaint)
        canvas.drawText("পরিমাণ", col3, yPos + 21f, tableHeaderPaint)
        canvas.drawText("দর", col4, yPos + 21f, tableHeaderPaint)
        canvas.drawText("টাকা", col5, yPos + 21f, tableHeaderPaint)
        yPos += 35f

        // Product rows
        val rowPaint = Paint().apply {
            color = colorText
            textSize = 13f
            typeface = banglaFont
            isAntiAlias = true
        }
        val rowLinePaint = Paint().apply {
            color = colorGray
            strokeWidth = 0.5f
        }

        order.productList.forEachIndexed { index, item ->
            // Alternate row background
            if (index % 2 == 0) {
                val altBg = Paint().apply { color = colorLightGray }
                canvas.drawRect(
                    margin, yPos - 3f,
                    pageWidth - margin, yPos + 24f, altBg
                )
            }
            canvas.drawText("${index + 1}", col1, yPos + 17f, rowPaint)
            canvas.drawText(item.productName, col2, yPos + 17f, rowPaint)
            canvas.drawText(item.quantity.toString(), col3, yPos + 17f, rowPaint)
            canvas.drawText(item.rate.toString(), col4, yPos + 17f, rowPaint)
            canvas.drawText("%.2f".format(item.amount), col5, yPos + 17f, rowPaint)
            canvas.drawLine(
                margin, yPos + 25f,
                pageWidth - margin, yPos + 25f, rowLinePaint
            )
            yPos += 30f
        }

        // Total row
        val totalBg = Paint().apply { color = Color.parseColor("#E8F4F6") }
        canvas.drawRect(margin, yPos, pageWidth - margin, yPos + 32f, totalBg)

        val totalPaint = Paint().apply {
            color = colorText
            textSize = 14f
            typeface = boldFont
            isAntiAlias = true
        }
        canvas.drawText("মোট টাকা", col4 - 50f, yPos + 22f, totalPaint)
        canvas.drawText("%.2f".format(order.totalAmount), col5, yPos + 22f, totalPaint)
        yPos += 50f

        // ══════════════════════════════════════════════════
        // SIGNATURE SECTION
        // ══════════════════════════════════════════════════
        yPos += 35f

        val sigLinePaint = Paint().apply {
            color = colorText
            strokeWidth = 1f
        }
        // Left signature line
        canvas.drawLine(margin + 10f, yPos, margin + 190f, yPos, sigLinePaint)
        // Right signature line
        canvas.drawLine(
            pageWidth / 2f + 20f, yPos,
            pageWidth / 2f + 200f, yPos, sigLinePaint
        )

        yPos += 14f
        val sigPaint = Paint().apply {
            color = colorText
            textSize = 12f
            typeface = banglaFont
            isAntiAlias = true
        }
        canvas.drawText("ম্যানেজার স্বাক্ষর", margin + 10f, yPos, sigPaint)
        canvas.drawText("রিসিভার স্বাক্ষর", pageWidth / 2f + 20f, yPos, sigPaint)

        // Finish PDF
        pdfDocument.finishPage(page)
        val outputStream = FileOutputStream(file)
        pdfDocument.writeTo(outputStream)
        pdfDocument.close()
        outputStream.close()

        return file
    }
}
