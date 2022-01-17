package infnet.android.smpa_permissao_serv

import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException

class LocalHoraViewModel : ViewModel() {
    val tempmock = Location("").apply {
        latitude = 42.0
        longitude = 442.0
    }
    val localizacoesLista = MutableLiveData<MutableList<Location>>(

    )

    private fun lerArquvo(caminhoSdCard:File?) {

        var linha: String?
        var textao = StringBuilder()
        textao.append('\n')
        try {

            val fl = File(caminhoSdCard, "DemoFile.txt")
            val arquivo = FileReader(fl)

            val buffer = BufferedReader(arquivo)

            /*var linhavazia=buffer.readLine().also { linha = it }*/
            while (buffer.readLine().also { linha = it } != null) {
                //myToast(linha)
            }
            buffer.close()
        } catch (ero: IOException) {
            //myToast("$ero")
        }
    }

    private fun arquivoCrdParaLocation(file:File): Location {
        val localHora = Location("")
        val arquivo = FileReader(file)
        var textoArquivo = StringBuilder()
        val buffer = BufferedReader(arquivo)

        var tmp_linha: String?
        while (buffer.readLine().also { tmp_linha = it } != null) {
            tmp_linha?.let{
                var separador = it.indexOf(":")
                var chave = it.substring(0,separador)
                var valor = it.substring(separador+1)

                if(chave.indexOf("latitude")==0){
                    localHora.latitude = valor.toDouble()
                }else if(chave.indexOf("longitude")==0){
                    localHora.longitude = valor.toDouble()
                }

                val data:String  = file.name.split(".crd")[0]
                localHora.time = data.toLong()

            }
        }
        return localHora
    }
     fun lerPasta(caminhoArquivos:Array<File>?){

        var lista = mutableListOf<Location>()

        if (caminhoArquivos != null) {
            var bigstr = ""
            for (file in caminhoArquivos){

                if(file.name.endsWith(".crd",true)
                    && file.name.length in 16..19    ){
                    lista.add(
                        arquivoCrdParaLocation(file) )
                    bigstr+="\n ${file.name}"
                }

            }
            localizacoesLista.postValue(lista)
        }

    }
}