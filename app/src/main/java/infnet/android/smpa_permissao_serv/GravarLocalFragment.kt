package infnet.android.smpa_permissao_serv

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import infnet.android.smpa_permissao_serv.databinding.FragmentFirstBinding
import java.io.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class GravarLocalFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var meuLocationManager:LocationManager
    val meuLocationListener = object : LocationListener {

        override fun onLocationChanged(location: Location) {
            Log.d("TD", "TD")
        }

        override fun onProviderDisabled(provider: String) {        }
        override fun onProviderEnabled(provider: String) {        }


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    private fun gravarLocalizacaoArquivo(ultimaLocal:Location) {
        val caminhoSdCard = requireActivity()?.getExternalFilesDir(null)
        myToast(caminhoSdCard.toString())
        val tempoAtual= ultimaLocal.time
        val file = File(caminhoSdCard, "$tempoAtual.crd")

        if (file.exists()) {
            file.delete()
        } else {
            try {
                val os: OutputStream = FileOutputStream(file)

                os.write("latitude: ${ultimaLocal.latitude}".toByteArray())
                os.write("\n".toByteArray())

                os.write("longitude: ${ultimaLocal.longitude}".toByteArray())
                os.write("\n".toByteArray())

                os.close()

            } catch (e: IOException) {
                Log.d("Permissao", "Erro de escrita em arquivo")
            }
        }
    }


    private fun ligaListenerProvedorDisponivel(
        lm: LocationManager,  listener: LocationListener, view: View
    ): Boolean {

        var provedorVar = LocationManager.GPS_PROVIDER
        val isGpsLigado: Boolean =  lm.isProviderEnabled(provedorVar)
        val contxt = requireContext()

        fun mostraRequisicaoGPS(){
            val REQUEST_PERMISSION_CODE = 128

            Snackbar.make(view, "GPS desativado, ative para salvar a localizaçao",
                Snackbar.LENGTH_SHORT)
                .setAction("Permitir GPS") {
                    ActivityCompat.requestPermissions(
                        requireActivity(), arrayOf(
                            Manifest
                                .permission.ACCESS_FINE_LOCATION
                        ), REQUEST_PERMISSION_CODE  )  }.show()
        }

        if (ActivityCompat.checkSelfPermission(
                contxt,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            &&
            ActivityCompat.checkSelfPermission(
                contxt,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {

            mostraRequisicaoGPS()
            return false
        }else{
            lm.requestLocationUpdates(provedorVar, 2000L,
                0f, meuLocationListener)
            var ultimaLocal = lm.getLastKnownLocation(provedorVar)

            if (ultimaLocal == null) {
                provedorVar=LocationManager.NETWORK_PROVIDER
                if(!lm.isProviderEnabled(provedorVar)){
                    mostraRequisicaoGPS()
                    return false
                }

                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000L,
                    0f, meuLocationListener)
                ultimaLocal = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            }
            val ts=
            ultimaLocal?.let{
                binding?.txtTelaRequisitaLocalizacao.text =
                    "Ultima Localizaçao : ${it.altitude}, " +
                            "${it.longitude}"
                gravarLocalizacaoArquivo(it)
            }

        }

        return true
    }

    private fun myToast(text: String?) {
        if (text != null)
            Toast.makeText(requireContext(), text, Toast.LENGTH_LONG + 3223).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        meuLocationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        binding.buttonFirst.setOnClickListener {
            //onCameraClick(it)
            ligaListenerProvedorDisponivel(meuLocationManager,  meuLocationListener,it)
        }

    }

    override fun onPause() {
        super.onPause()
        println("removendo listener parar consumo de bateria")
        meuLocationManager.removeUpdates(meuLocationListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}