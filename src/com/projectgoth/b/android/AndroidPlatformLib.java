package com.projectgoth.b.android;

import com.projectgoth.b.PlatformLib;

public class AndroidPlatformLib extends PlatformLib {

	public String formatString(String s, String[] args) {
		return String.format(s, (Object[]) args);
	}
}
