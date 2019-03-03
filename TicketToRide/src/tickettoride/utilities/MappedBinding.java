package tickettoride.utilities;

import java.util.function.BiFunction;
import java.util.function.Function;

import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ObservableValue;

public class MappedBinding {

	public static <I, O> Binding<O> createBinding(ObservableValue<I> observable, Function<I, O> mapping) {
		return Bindings.createObjectBinding(() -> mapping.apply(observable.getValue()), observable);
	}
	
	public static <I1, I2, O> Binding<O> createBinding(ObservableValue<I1> obs1, ObservableValue<I2> obs2, 
														BiFunction<I1, I2, O> mapping) {
		return Bindings.createObjectBinding(() -> mapping.apply(obs1.getValue(), obs2.getValue()), obs1, obs2);
	}
	
	public static <I> DoubleBinding createDoubleBinding(ObservableValue<I> observable, Function<I, Double> mapping) {
		return Bindings.createDoubleBinding(() -> mapping.apply(observable.getValue()), observable);
	}
	
	public static <I1, I2> DoubleBinding createDoubleBinding(ObservableValue<I1> obs1, ObservableValue<I2> obs2, 
														BiFunction<I1, I2, Double> mapping) {
		return Bindings.createDoubleBinding(() -> mapping.apply(obs1.getValue(), obs2.getValue()), obs1, obs2);
	}
}
