package org.mazur.gromula

import java.awt.Font/**
 * Utils.
 * 
 * Version: $Id$
 *
 * @author Roman Mazur (mailto: mazur.roman@gmail.com)
 *
 */

static Font createCodeFont(int size) { return new Font(Font.MONOSPACED, Font.PLAIN, size) }
static Font createCodeFont() { return createCodeFont(12) } 
 