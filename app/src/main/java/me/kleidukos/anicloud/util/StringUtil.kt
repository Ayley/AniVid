package me.kleidukos.anicloud.util

class StringUtil {

    companion object{
        fun prepare(string: String?): String{
            if(string == null){
                return ""
            }
            return string.replace("\'","|").replace("\"","||").replace("\'", "").replace("\\","").replace("’", "|||").replace("é", "||||")
        }

        fun serialize(string: String?): String{
            if(string == null){
                return ""
            }
            return string.replace("||||", "é").replace("|||", "´").replace("||", "\"").replace("|", "\'")
        }
    }
}