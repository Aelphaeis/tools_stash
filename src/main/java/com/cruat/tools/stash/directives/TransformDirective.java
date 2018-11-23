package com.cruat.tools.stash.directives;

import com.cruat.tools.stash.config.Instruction;
import com.cruat.tools.stash.transformer.Transformer;

public interface TransformDirective extends Directive {
	void execute(Instruction i, Transformer t);
	
}
