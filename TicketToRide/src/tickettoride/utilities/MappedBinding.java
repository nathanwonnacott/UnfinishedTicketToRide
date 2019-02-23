package tickettoride.utilities;

import java.util.function.Function;

import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;

public class MappedBinding {

	public static <I, O> Binding<O> createBinding(ObservableValue<I> observable, Function<I, O> mapping) {
		return Bindings.createObjectBinding(() -> mapping.apply(observable.getValue()), observable);
	}
}
