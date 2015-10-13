package com.confluex.zuul.data.config

class ZuulDataConstants {

    static final long API_VERSION = 3L

    static final String ROLE_GUEST = "ROLE_GUEST"
    static final String ROLE_USER = "ROLE_USER"
    static final String ROLE_ADMIN = "ROLE_ADMIN"
    static final String ROLE_SYSTEM_ADMIN = "ROLE_SYSTEM_ADMIN"

    static final String PERMISSION_ADMIN = 'admin'

    static final String KEY_ALGORITHM_PGP = "PGP"
    static final String KEY_ALGORITHM_AES = "PBEWITHSHA256AND128BITAES-CBC-BC"
    static final String KEY_ALGORITHM_3DES_BC = "PBEWithSHAAnd2-KeyTripleDES-CBC"
    static final String KEY_ALGORITHM_3DES_JCE = "PBEWithMD5AndTripleDES"
    static final String KEY_ALGORITHM_DES = "PBEWithMD5AndDES"

    static final List<String> PGP_KEY_ALGORITHMS = [
            KEY_ALGORITHM_PGP
    ]
    static final List<String> PBE_KEY_ALGORITHMS = [
            KEY_ALGORITHM_AES,
            KEY_ALGORITHM_3DES_BC,
            KEY_ALGORITHM_3DES_JCE,
            KEY_ALGORITHM_DES
    ]

}
