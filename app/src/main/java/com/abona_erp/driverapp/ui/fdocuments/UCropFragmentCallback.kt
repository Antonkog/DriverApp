package com.abona_erp.driverapp.ui.fdocuments

import com.abona_erp.driverapp.ui.fdocuments.UCropFragment.UCropResult

interface UCropFragmentCallback {
    /**
     * Return cropping result or error
     *
     * @param result
     */
    fun onCropFinish(result: UCropResult?)
}