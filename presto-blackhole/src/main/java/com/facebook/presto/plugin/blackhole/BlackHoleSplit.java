/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.presto.plugin.blackhole;

import com.facebook.presto.spi.ConnectorSplit;
import com.facebook.presto.spi.HostAddress;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

public final class BlackHoleSplit
        implements ConnectorSplit
{
    private int pagesCount;
    private final int rowsPerPage;

    @JsonCreator
    public BlackHoleSplit(
            @JsonProperty("pagesCount") int pagesCount,
            @JsonProperty("rowsPerPage") int rowsPerPage)
    {
        this.rowsPerPage = checkNotNull(rowsPerPage, "rowsPerPage is null");
        this.pagesCount = checkNotNull(pagesCount, "pagesCount is null");
    }

    @JsonProperty
    public int getPagesCount()
    {
        return pagesCount;
    }

    @JsonProperty
    public int getRowsPerPage()
    {
        return rowsPerPage;
    }

    @Override
    public boolean isRemotelyAccessible()
    {
        return true;
    }

    @Override
    public List<HostAddress> getAddresses()
    {
        return ImmutableList.of();
    }

    @Override
    public Object getInfo()
    {
        return this;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(getPagesCount(), getRowsPerPage());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        BlackHoleSplit other = (BlackHoleSplit) obj;
        return Objects.equals(this.getPagesCount(), other.getPagesCount()) &&
                Objects.equals(this.getRowsPerPage(), other.getRowsPerPage());
    }
}