package com.example.perfectweather.ui.cities

import android.R.array
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.*
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.perfectweather.R
import kotlinx.android.synthetic.main.fragment_cities.*

class CitiesFragment : Fragment() {

    private lateinit var citiesViewModel: CitiesViewModel
    var button: Button? = null
    var button1: Button? = null
    val APP_PREFERENCES = "WeatherApp"
    val APP_PREFERENCES_CurrentCity = ""
    var likedCities : ArrayList<String> = arrayListOf()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        citiesViewModel = ViewModelProviders.of(this).get(CitiesViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_cities, container, false)

        val mSettings = getActivity()?.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
        for (i in 0 until mSettings?.getInt("StringArrayLength", 0)!!) {
            likedCities.add(mSettings?.getString("StringArrayElement"+i, "")!!).toString()
        }

        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val mArrayAdapter = ArrayAdapter<Any>(requireActivity(), android.R.layout.simple_list_item_1, likedCities as List<Any> )
        listView.setAdapter(mArrayAdapter)

        listView.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
            val mSettings = getActivity()?.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
            val editor = mSettings?.edit()
            editor?.putString(APP_PREFERENCES_CurrentCity, likedCities.get(position))
            editor?.commit()
            Toast.makeText(
                activity,
                getString(R.string.saved),
                Toast.LENGTH_SHORT
            ).show()
        })

        listView.onItemLongClickListener =
            OnItemLongClickListener { arg0, arg1, pos, id ->
                likedCities.removeAt(pos)
                mArrayAdapter.notifyDataSetChanged()

                val mSettings = getActivity()?.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
                val editor = mSettings?.edit()
                for (i in 0 until likedCities.size) {
                    editor?.putString("StringArrayElement$i", likedCities[i])
                }
                editor?.putInt("StringArrayLength", likedCities.size);
                editor?.commit();

                Toast.makeText(
                    activity,
                    getString(R.string.deleted),
                    Toast.LENGTH_SHORT
                ).show()
                true
            }

        selectCity_spinner.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>,
                view: View,
                i: Int,
                l: Long
            ) {
                val mSettings = getActivity()?.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
                val editor = mSettings?.edit()
                editor?.putString(APP_PREFERENCES_CurrentCity, adapterView.adapter.getItem(i).toString())
                editor?.commit()
                Toast.makeText(
                    activity,
                    getString(R.string.saved),
                    Toast.LENGTH_SHORT
                ).show()

                if(!likedCities.contains(adapterView.adapter.getItem(i).toString())) {
                    likedCities.add(adapterView.adapter.getItem(i).toString())
                    mArrayAdapter.notifyDataSetChanged()

                    val mSettings = getActivity()?.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE)
                    val editor = mSettings?.edit()
                    for (i in 0 until likedCities.size) {
                        editor?.putString("StringArrayElement$i", likedCities[i])
                    }
                    editor?.putInt("StringArrayLength", likedCities.size);
                    editor?.commit();
                }
            }
            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        })
    }
}