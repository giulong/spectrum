package io.github.giulong.spectrum.internals.page_factory;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.internal.LocatingElementHandler;

import java.lang.reflect.Method;

@Slf4j
public class SpectrumLocatingElementHandler extends LocatingElementHandler {

    private final boolean secured;

    public SpectrumLocatingElementHandler(final ElementLocator locator, final boolean secured) {
        super(locator);
        this.secured = secured;
    }

    @Override
    public Object invoke(final Object object, Method method, final Object[] args) throws Throwable {
        if (secured && "sendKeys".equals(method.getName())) {
            log.trace("Intercepting sendKeys on a @Secured WebElement");
            final CharSequence firstCharSequence = ((CharSequence[]) args[0])[0];
            ((CharSequence[]) args[0])[0] = String.format("@Secured@%s@Secured@", firstCharSequence);
        }

        return super.invoke(object, method, args);
    }
}
