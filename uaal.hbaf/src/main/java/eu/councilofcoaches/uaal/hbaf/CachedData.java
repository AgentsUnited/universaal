/*
Copyright 2020 SABIEN - Universitat Politecnica de Valencia.
This file is part of Agents United universAAL Relay

Agents United universAAL Relay is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Agents United universAAL Relay is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with Agents United universAAL Relay.  If not, see <https://www.gnu.org/licenses/>.
 */
package eu.councilofcoaches.uaal.hbaf;

public class CachedData {
    protected Float cacheDiastolic;
    protected Float cacheSystolic;
    protected Integer cacheRate;
    protected Long cacheTIMEbp=1l;
    protected Long cacheTIMEhr=1l;
    
    protected static CachedData from(CachedData cd){
	if(cd!=null){
	    return cd;
	}else{
	    return new CachedData();
	}
    }
}
