package com.example.compereirowww.inventory20181.Activities;

import com.example.compereirowww.inventory20181.DataBase.DB;
import com.example.compereirowww.inventory20181.DataBase.DB.IT.StateValues;

public class Tips {

    public static final String tip1 = "Los archivos .csv a importar deben unbicarse en la " +
            "carpeta \"Para Importar\" de la aplicación: \n" +
            AppStatics.db.getPreference(DB.PT.PNames.TO_IMPORT_DIRECTORY_PATH);

    public static final String tip2 = "Si el estado de un número es " +
            StateValues.toString(StateValues.LEFTOVER) +
            " o " + StateValues.toString(StateValues.LEFTOVER_PRESENT) +
            " solo puede ser cambiado a otro si se importa un archivo UH y este está " +
            "presente en el mismo!";

    public static final String tip3 = "Si el estado de un número es " +
            StateValues.toString(StateValues.MISSING) +
            " o " + StateValues.toString(StateValues.IGNORED_MISSING) +
            " solo puede ser cambiado a " + StateValues.toString(StateValues.PRESENT) +
            " al leer el QR de dicho número";

    public static final String ti4 = "";

}
