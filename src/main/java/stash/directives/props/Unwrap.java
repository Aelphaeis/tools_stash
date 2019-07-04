package stash.directives.props;

import stash.config.Instruction;
import stash.directives.Directive;
import stash.directives.InstructionDirective;
import stash.directives.Supports;
import stash.transformer.PropertyTransformer;

@Supports({ PropertyTransformer.class })
public class Unwrap implements InstructionDirective, Directive{

	@Override
	public Instruction execute(Instruction i) {
		String value = i.getValue();
		value = value.substring(1, value.length() - 1);
		return new Instruction(i.getKey(), value, i.getLocation());
	}
}
