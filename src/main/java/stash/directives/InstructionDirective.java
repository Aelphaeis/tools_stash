package com.cruat.tools.stash.directives;

import com.cruat.tools.stash.config.Instruction;

public interface InstructionDirective extends Directive {
	Instruction execute(Instruction i);
}
