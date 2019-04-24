package com.unwrappedapps.android.spreadsheet.ui.sheet

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.widget.TextView
import com.unwrappedapps.android.spreadsheet.R
import com.unwrappedapps.android.spreadsheet.spreadsheet.Spreadsheet
import android.view.Gravity


class SheetAdapter(val density: Int, var select: TextView) :
    RecyclerView.Adapter<SheetAdapter.ViewHolder>() {

    companion object {
        private var spreadsheet: Spreadsheet? = null

        private const val CELL = 3
        private const val COLUMN_MARKER = 2
        private const val ROW_MARKER = 1

        //30 can do 3 digits
        //36 can do 4 digits
        //private val ROW_MARKER_WIDTH = 45
        private const val ROW_MARKER_WIDTH = 50
    }

    init {
        select.text = ""
    }

    fun assignSpreadsheet(ss : Spreadsheet?) {
        spreadsheet = ss
    }

    override fun getItemViewType(position: Int): Int {

        val coordinate = posToMarkers(position)

        return if (coordinate.r == 0) {
            COLUMN_MARKER
        } else if (coordinate.c == 0) {
            ROW_MARKER
        } else {
            CELL
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        // Create a new view.
        // could change markers by viewType here if necessary
        val view: View

        view = LayoutInflater.from(parent.getContext())
            .inflate(com.unwrappedapps.android.spreadsheet.R.layout.graph_cell, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        var items: Int

        items = 2147483647 // biggest int I can make
        items--
        items--
        return items
    }

    // what to do when position is bound to viewholder
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (getItemViewType(position) == CELL) {
            makeCell(holder, position)
        } else if (getItemViewType(position) == COLUMN_MARKER) {
            makeColumnMarker(holder, position)
        } else {
            makeRowMarker(holder, position)
        }

    }


    private fun makeCell(viewHolder: ViewHolder, position: Int) {

        val workbook = spreadsheet?.workbook
        val sheet = workbook?.sheetList?.get(workbook.currentSheet)

        val numColumns = sheet?.getNumberOfColumns()

        val coordinate = posToMarkers(position)
        var r = coordinate.r
        var c = coordinate.c

        r--
        c--

        val cellsValue: String?

        if (numColumns == null) return

        if (numColumns < 1) {
            cellsValue = " "
        } else {
            cellsValue = sheet.getRow(r).getCell(c).cellValue
        }

        viewHolder.textView.text = cellsValue

        val width = ROW_MARKER_WIDTH * density

        viewHolder.textView.width = width*2

        viewHolder.textView.height = density * 60

        //val alpha = 0;
        val alpha = 255

        val sdk = android.os.Build.VERSION.SDK_INT
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            viewHolder.textView.setBackgroundDrawable(ColorDrawable(Color.argb(alpha, 255, 255, 255)))

        } else {
            viewHolder.textView.setBackground(ColorDrawable(Color.argb(alpha, 255, 255, 255)))
        }

        viewHolder.textView.setOnClickListener(
            {
                select.text = cellsValue
            })
    }


    private fun makeRowMarker(viewHolder: ViewHolder, position: Int) {

        val (row) = posToMarkers(position)

        viewHolder.textView.height = 30 * density

        setBackground(viewHolder)

        val rm = Integer.toString(row)

        val width = ROW_MARKER_WIDTH * density

        viewHolder.textView.height = density * 60

        viewHolder.textView.setWidth(width)
        viewHolder.textView.setText(rm)
        viewHolder.textView.setGravity(Gravity.CENTER)

    }


    private fun makeColumnMarker(viewHolder: ViewHolder, position: Int) {

        val (_, c) = posToMarkers(position)

        // must be wide enough for 2-3 letter column markers

        val width = ROW_MARKER_WIDTH * density

        if (position == 0) {
            viewHolder.textView.width = width
        } else {
            viewHolder.textView.width = width*2
        }

        setBackground(viewHolder)

        val lastPosition = (c - 1) % 26

        var text = getLetter(lastPosition)

        if (c > 26) {
            var before = (c - 1) / 26
            before--

            if (before >= 26) {
                before = before % 26
            }

            val beforeLast = getLetter(before)
            text = beforeLast + text

        } else if (position == 0) {
            text = " "
        }


        if (c > 26 * (26 + 1)) {
            var ch = c
            ch = ch - 26
            ch = ch - 1
            ch = ch / 26
            ch = ch / 26
            ch = ch - 1

            val beforebeforeLast = getLetter(ch)
            text = beforebeforeLast + text

        } else if (position == 0) {
            text = " "
        }

        viewHolder.textView.setGravity(Gravity.CENTER)
        viewHolder.textView.setText(text)
    }


    private fun setBackground(viewHolder: ViewHolder) {

        //val gray = 84
        val gray = 193

        val sdk = android.os.Build.VERSION.SDK_INT
        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            //setBackgroundDrawable();
            viewHolder.textView.setBackgroundDrawable(ColorDrawable(Color.argb(255, gray,gray,gray)))

        } else {
            viewHolder.textView.setBackground(ColorDrawable(Color.argb(255, gray,gray,gray)))

        }
    }

    private fun getLetter(lastPosition: Int): String {

        val text: String

        when (lastPosition) {
            0 -> text = "A"
            1 -> text = "B"
            2 -> text = "C"
            3 -> text = "D"
            4 -> text = "E"
            5 -> text = "F"
            6 -> text = "G"
            7 -> text = "H"
            8 -> text = "I"
            9 -> text = "J"
            10 -> text = "K"
            11 -> text = "L"
            12 -> text = "M"
            13 -> text = "N"
            14 -> text = "O"
            15 -> text = "P"
            16 -> text = "Q"
            17 -> text = "R"
            18 -> text = "S"
            19 -> text = "T"
            20 -> text = "U"
            21 -> text = "V"
            22 -> text = "W"
            23 -> text = "X"
            24 -> text = "Y"
            25 -> text = "Z"
            else -> text = " "
        }
        return text
    }


    // translate position to column and row
    private fun posToMarkers(pos: Int): Coordinate {
        val diff: Int
        val column: Int

        var p = pos
        p++
        p = p * 2
        val r = Math.floor(Math.sqrt(p.toDouble()))
        val w = r.toInt()

        val tryit = (w + 1) * w / 2

        if (tryit > pos) {
            val low = (w - 1) * w / 2
            diff = low - pos
            column = w - 1 + diff
        } else {
            diff = tryit - pos
            column = w + diff
        }

        val row = diff * -1

        return Coordinate(row, column)
    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView

        init {
            textView = view.findViewById(R.id.textView)
        }
    }

    private data class Coordinate(val r : Int, val c : Int)

}