package io.github.giulong.spectrum.internals.page_factory;

import io.github.giulong.spectrum.interfaces.Secured;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WrapsElement;
import org.openqa.selenium.interactions.Locatable;
import org.openqa.selenium.support.pagefactory.DefaultFieldDecorator;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class SpectrumFieldDecorator extends DefaultFieldDecorator {

    public SpectrumFieldDecorator(final ElementLocatorFactory factory) {
        super(factory);
    }

    @Override
    public Object decorate(final ClassLoader loader, final Field field) {
        final ElementLocator locator = factory.createLocator(field);
        if (locator == null) {
            return null;
        }

        if (WebElement.class.isAssignableFrom(field.getType())) {
            final InvocationHandler handler = new SpectrumLocatingElementHandler(locator, field.isAnnotationPresent(Secured.class));
            return Proxy.newProxyInstance(loader, new Class[]{WebElement.class, WrapsElement.class, Locatable.class}, handler);
        }

        if (isDecoratableList(field)) {
            return proxyForListLocator(loader, locator);
        }

        return null;
    }
}
