package discover;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ClassDiscover {
    public static List<String> retrieveAllClasses(Class<?> sourceClass){
        return packageExplorer(sourceClass.getPackageName());
    }

    public static List<String> packageExplorer(String packageName){
        ArrayList<String> classNames = new ArrayList<String>();
        try {
        	// Pegar o diretório/caminho base do pacote, e packageName é definido como uma pasta/caminho (lendo um recurso/arquivo que está localizado em um determinado pacote no classpath do sistema)
            InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(packageName.replaceAll("\\.", "/"));
            
            
            // Fazendo varredura e tomando oações em tudo que tem no diretório/pasta
            
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            String linha;
            
            while ((linha = br.readLine())!= null) {
                if (linha.endsWith(".class")) {
                    classNames.add(packageName+ "." +linha.substring(0, linha.indexOf(".class")));
                }
                else {
                    classNames.addAll(packageExplorer(packageName+"."+linha));
                }
            }
            return classNames;
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
