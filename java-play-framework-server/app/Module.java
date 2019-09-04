import com.google.inject.AbstractModule;

import controllers.*;

public class Module extends AbstractModule {

    @Override
    protected void configure() {
        bind(TransformerApiControllerImpInterface.class).to(TransformerApiControllerImp.class);
    }
}