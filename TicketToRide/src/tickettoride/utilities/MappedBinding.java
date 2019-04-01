package tickettoride.utilities;

import java.util.function.BiFunction;
import java.util.function.Function;

import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ObservableValue;

/**
 * Provides some static utilities for creating bindings that are based on mapping functions from observable
 * values.
 * @author nate
 *
 */
public class MappedBinding {

	/**
	 * Creates a {@link Binding} that results in the specified mapping function being applied to the
	 * value of the specified observable. The binding is invalidated whenever the observable is changed.
	 * @param observable
	 * @param mapping
	 * @return
	 */
	public static <I, O> Binding<O> createBinding(ObservableValue<I> observable, Function<I, O> mapping) {
		return Bindings.createObjectBinding(() -> mapping.apply(observable.getValue()), observable);
	}
	
	/**
	 * Creates a {@link Binding} that results in the specified binary mapping function being applied to the
	 * value of the 2 specified observables. The binding is invalidated whenever either observable is changed.
	 * @param observable
	 * @param mapping
	 * @return
	 */
	public static <I1, I2, O> Binding<O> createBinding(ObservableValue<I1> obs1, ObservableValue<I2> obs2, 
														BiFunction<I1, I2, O> mapping) {
		return Bindings.createObjectBinding(() -> mapping.apply(obs1.getValue(), obs2.getValue()), obs1, obs2);
	}
	
	/**
	 * Creates a {@link DoubleBinding} that results in the specified mapping function being applied to the
	 * value of the specified observable. The binding is invalidated whenever the observable is changed.
	 * @param observable
	 * @param mapping
	 * @return
	 */
	public static <I> DoubleBinding createDoubleBinding(ObservableValue<I> observable, Function<I, Double> mapping) {
		return Bindings.createDoubleBinding(() -> mapping.apply(observable.getValue()), observable);
	}
	
	/**
	 * Creates a {@link DoubleBinding} that results in the specified binary mapping function being applied to the
	 * value of the 2 specified observables. The binding is invalidated whenever either observable is changed.
	 * @param observable
	 * @param mapping
	 * @return
	 */
	public static <I1, I2> DoubleBinding createDoubleBinding(ObservableValue<I1> obs1, ObservableValue<I2> obs2, 
														BiFunction<I1, I2, Double> mapping) {
		return Bindings.createDoubleBinding(() -> mapping.apply(obs1.getValue(), obs2.getValue()), obs1, obs2);
	}
}
