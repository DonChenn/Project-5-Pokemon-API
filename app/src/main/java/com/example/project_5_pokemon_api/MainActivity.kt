package com.example.project_5_pokemon_api

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers

import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    var pokemonImageURL = ""
    var pokemonName = ""
    var pokemonType = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.generateButton)
        val imageView = findViewById<ImageView>(R.id.randomPokemon)
        val nameTextView = findViewById<TextView>(R.id.pokemonName)
        val typeTextView = findViewById<TextView>(R.id.pokemonType)

        getNextImage(button, imageView, nameTextView, typeTextView)
    }

    private fun getRandomPokemonIdOrName(): String {
        val randomId = Random.nextInt(1, 1017)
        return randomId.toString()
    }

    private fun getPokemonDetails(pokemonIdOrName: String) {
        val client = AsyncHttpClient()

        val apiUrl = "https://pokeapi.co/api/v2/pokemon/$pokemonIdOrName/"

        client.get(apiUrl, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers, json: JsonHttpResponseHandler.JSON) {
                val sprites = json.jsonObject.getJSONObject("sprites")
                val imageURL = sprites.getString("front_default")
                pokemonImageURL = imageURL

                pokemonName = json.jsonObject.getString("name")

                val typesArray = json.jsonObject.getJSONArray("types")
                pokemonType = if (typesArray.length() > 0) {
                    val firstTypeObject = typesArray.getJSONObject(0).getJSONObject("type")
                    firstTypeObject.getString("name")
                } else {
                    "Unknown Type"
                }

                Log.d("pokemonImageURL", "Pokemon image URL set")
                Log.d("Pokemon", "Response successful: $json")
            }

            override fun onFailure(statusCode: Int, headers: Headers?, errorResponse: String, throwable: Throwable?) {
                Log.d("Pokemon Error", errorResponse)
            }
        })
    }

    private fun getNextImage(button: Button, imageView: ImageView, nameTextView: TextView, typeTextView: TextView) {
        button.setOnClickListener {
            val randomPokemonIdOrName = getRandomPokemonIdOrName()
            getPokemonDetails(randomPokemonIdOrName)

            Glide.with(this)
                .load(pokemonImageURL)
                .fitCenter()
                .into(imageView)

            nameTextView.text = "Name: $pokemonName"
            typeTextView.text = "Type: $pokemonType"
        }
    }
}
