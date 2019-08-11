package com.project.capstone.client;

import java.util.List;
import java.util.Optional;

import org.springframework.util.CollectionUtils;

import com.project.capstone.model.Shift;

public class ShiftValidator {
	private ShiftValidator() {}
	public static boolean isValid(int shiftId, List<Shift> shifts) {
		if(!CollectionUtils.isEmpty(shifts)) {
			Optional<Shift> opShift = shifts.stream().filter(s->s.getShiftId()==shiftId).findAny();
			return opShift.isPresent();
		}
		return false;
	}
}
