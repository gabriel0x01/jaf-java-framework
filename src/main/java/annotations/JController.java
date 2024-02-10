package annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // Tipo de retençao compilacao ex.: override / execucao ex.: enquanto a aplicacao esta rodando
@Target(ElementType.TYPE) // Aplicado a uma instancia de uma classe
public @interface JController {

}
