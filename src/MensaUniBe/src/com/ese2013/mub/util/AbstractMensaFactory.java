package com.ese2013.mub.util;

import java.util.List;

import com.ese2013.mub.model.Mensa;

/**
 * Abstract Mensa factory class. A class which extends this class should
 * implement "createMensaList" to return a complete List of Mensas, including
 * WeeklyMenuplans.
 */
public abstract class AbstractMensaFactory {
	public abstract List<Mensa> createMensaList() throws MensaDownloadException, MensaLoadException;
}