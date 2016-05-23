DESCRIPTION = "Resin helpers for running runqemu"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${RESIN_COREBASE}/COPYING.Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI = " \
    file://runqemu-resin \
    file://run-dhcp.patch \
    file://yes-reboot.patch \
    "
S = "${WORKDIR}"

inherit allarch deploy

DEPENDS = " \
    qemu-helper-native \
    dhcp-native \
    "

POKY_SCRIPTS = " \
    runqemu \
    runqemu-internal \
    runqemu-ifup \
    runqemu-ifdown \
    "

do_unpack_extra() {
    # Detect poky layer path
    poky_path=""
    for layer in ${BBLAYERS}; do
        case $layer in
            *poky/meta)
                poky_path="$layer"
                break
                ;;
            *)
                ;;
        esac
    done
    if [ -z "$poky_path" ]; then
        bbfatal "Can't detect poky layer path"
    fi

    # Copy needed scripts
    poky_scripts_path=$poky_path/../scripts
    for script in ${POKY_SCRIPTS}; do
        cp $poky_scripts_path/$script ${WORKDIR}
    done
}

do_deploy() {
    deploydir=${DEPLOYDIR}/resin-runqemu

    mkdir -p $deploydir/{${bindir_native},${sbindir_native}}
    for script in ${POKY_SCRIPTS}; do
        cp ${WORKDIR}/$script $deploydir
    done
    cp ${WORKDIR}/runqemu-resin $deploydir

    # Deploy helper for qemu
    cp ${STAGING_BINDIR_NATIVE}/tunctl $deploydir/${bindir_native}/tunctl
    cp ${STAGING_SBINDIR_NATIVE}/dhcpd $deploydir/${sbindir_native}/dhcpd
}

addtask unpack_extra after do_unpack before do_patch
addtask deploy before do_package after do_install
