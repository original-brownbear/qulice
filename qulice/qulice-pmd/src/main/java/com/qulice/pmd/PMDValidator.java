/**
 * Copyright (c) 2011-2012, Qulice.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the Qulice.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.qulice.pmd;

import com.jcabi.log.Logger;
import com.qulice.spi.Environment;
import com.qulice.spi.ValidationException;
import com.qulice.spi.Validator;
import java.io.File;
import java.util.Collection;
import net.sourceforge.pmd.DataSource;
import net.sourceforge.pmd.IRuleViolation;

/**
 * Validates source code with PMD.
 *
 * @author Yegor Bugayenko (yegor@qulice.com)
 * @version $Id$
 */
public final class PMDValidator implements Validator {

    /**
     * Validator.
     */
    private final transient SourceValidator validator = new SourceValidator();

    /**
     * {@inheritDoc}
     */
    @Override
    public void validate(final Environment env) throws ValidationException {
        final Collection<DataSource> sources = this.getSources(env);
        this.validateSources(env, sources);
        this.checkViolations();
        Logger.info(
            this,
            "No PMD violations found in %d files",
            sources.size()
        );
    }

    /**
     * Checks validation results. If violations were found
     * <code>ValidationException</code> is thrown.
     * @throws ValidationException If violations were found.
     */
    private void checkViolations() throws ValidationException {
        final Collection<IRuleViolation> violations =
            this.validator.getViolations();
        final int size = violations.size();
        if (!violations.isEmpty()) {
            throw new ValidationException(
                "%d PMD violations (see log above)",
                size
            );
        }
    }

    /**
     * Retrieves <code>DataSource</code>s from <code>Environment</code>.
     * @param environment Environment.
     * @return Collection of <code>DataSource</code>s of source files.
     */
    private Collection<DataSource> getSources(final Environment environment) {
        final Files files = new Files();
        final Collection<DataSource> sources = files.getSources(environment);
        if (sources.isEmpty()) {
            Logger.info(this, "No files to check with PMD");
        }
        return sources;
    }

    /**
     * Performs validation of the specified source files.
     * @param environment Environment.
     * @param sources Source files to validate.
     */
    private void validateSources(
        final Environment environment,
        final Collection<DataSource> sources
    ) {
        final File base = environment.basedir();
        final String path = base.getPath();
        this.validator.validate(sources, path);
    }
}
