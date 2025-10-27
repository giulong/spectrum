package io.github.giulong.spectrum.internals.page_factory;

import java.lang.reflect.Method;

import lombok.extern.slf4j.Slf4j;

import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.internal.LocatingElementHandler;

@Slf4j
public class SpectrumLocatingElementHandler extends LocatingElementHandler {

    private final ElementLocator elementLocator;
    private final boolean secured;

    public SpectrumLocatingElementHandler(final ElementLocator elementLocator, final boolean secured) {
        super(elementLocator);
        this.elementLocator = elementLocator;
        this.secured = secured;
    }

    @Override
    public Object invoke(final Object proxy, Method method, final Object[] args) throws Throwable {
        if ("toString".equals(method.getName())) {
            return method.invoke(elementLocator, args);
        }

        if (secured && "sendKeys".equals(method.getName())) {
            log.trace("Intercepting sendKeys on a @Secured WebElement");
            final CharSequence firstCharSequence = ((CharSequence[]) args[0])[0];
            ((CharSequence[]) args[0])[0] = String.format("@Secured@%s@Secured@", firstCharSequence);
        }

        return super.invoke(proxy, method, args);
    }
}
