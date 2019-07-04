package stash.directives;

import stash.config.Instruction;

public interface InstructionDirective extends Directive {
	Instruction execute(Instruction i);
}
