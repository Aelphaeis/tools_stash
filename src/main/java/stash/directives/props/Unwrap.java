package com.cruat.tools.stash.directives.props;

import com.cruat.tools.stash.config.Instruction;
import com.cruat.tools.stash.directives.Directive;
import com.cruat.tools.stash.directives.InstructionDirective;
import com.cruat.tools.stash.directives.Supports;
import com.cruat.tools.stash.transformer.PropertyTransformer;

@Supports({ PropertyTransformer.class })
public class Unwrap implements InstructionDirective, Directive{

	@Override
	public Instruction execute(Instruction i) {
		String value = i.getValue();
		value = value.substring(1, value.length() - 1);
		return new Instruction(i.getKey(), value, i.getLocation());
	}
}
