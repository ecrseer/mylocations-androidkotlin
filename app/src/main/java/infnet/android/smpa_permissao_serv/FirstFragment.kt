package infnet.android.smpa_permissao_serv

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
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
import java.util.*

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    val listnr = object : LocationListener {

        override fun onLocationChanged(location: Location) {
            Log.d("LOLOL", "EITAITA")

            val d = 0;
        }

        override fun onProviderDisabled(provider: String) {
            super.onProviderDisabled(provider)
            val d = 2
        }

        override fun onProviderEnabled(provider: String) {
            super.onProviderEnabled(provider)
            val d = 2
        }


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

                os.write("${ultimaLocal.toString()}".toByteArray())
                os.write("\n".toByteArray())

                os.close()

            } catch (e: IOException) {
                Log.d("Permissao", "Erro de escrita em arquivo")
            }
        }
    }

    private fun lerArquvo() {
        val caminhoSdCard = requireActivity()?.getExternalFilesDir(null)

        var linha: String?
        var textao = StringBuilder()
        textao.append('\n')
        try {

            val fl = File(caminhoSdCard, "DemoFile.txt")
            val arquivo = FileReader(fl)

            val buffer = BufferedReader(arquivo)

            /*var linhavazia=buffer.readLine().also { linha = it }*/
            while (buffer.readLine().also { linha = it } != null) {
                myToast(linha)
            }
            buffer.close()
        } catch (ero: IOException) {
            myToast("$ero")
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

            Snackbar.make(view, "GPS desativado, clique aqui para habilitar",
                Snackbar.LENGTH_SHORT)
                .setAction("Habilita GPS") {
                    ActivityCompat.requestPermissions(
                        requireActivity(), arrayOf(
                            Manifest
                                .permission.ACCESS_FINE_LOCATION
                        ), REQUEST_PERMISSION_CODE
                    )
                }.show()
        }

        if (ActivityCompat.checkSelfPermission(
                contxt,
                Manifest.permission.ACCESS_FINE_LOCATION
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
                0f, listnr)
            var ultimaLocal = lm.getLastKnownLocation(provedorVar)

            if (ultimaLocal == null) {
                provedorVar=LocationManager.NETWORK_PROVIDER
                if(!lm.isProviderEnabled(provedorVar)){
                    mostraRequisicaoGPS()
                    return false
                }

                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000L,
                    0f, listnr)
                ultimaLocal = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            }
            val ts=
            ultimaLocal?.let{
                binding?.txtTelaRequisitaLocalizacao.text =
                    "Ultima Localizaçao : ${it.altitude}, " +
                            "${it.longitude}"
                gravarLocalizacaoArquivo(it)
            }
            val d =2

        }

        return true
    }


    private fun ligaLocalizacaoListener(view: View) {
        val lm = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        //val gpsHabilitado:Boolean=lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
           ligaListenerProvedorDisponivel(lm,  listnr, view)

    }



    private fun myToast(text: String?) {
        if (text != null)
            Toast.makeText(requireContext(), text, Toast.LENGTH_LONG + 3223).show()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.buttonFirst.setOnClickListener {

            ligaLocalizacaoListener(it)
            // createDeleteFile()
            /*startActivity(
                    Intent(requireActivity(),PermissaoActivity::class.java)
                )*/
            //findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }
        binding.lerArqv.setOnClickListener {
            lerArquvo()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}